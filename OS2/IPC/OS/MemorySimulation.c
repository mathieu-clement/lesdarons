/*
 =============================================
 Name        : MainMemory.c
 Author      : Clément & Piller
 Version     : V1.0
 =============================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <string.h>
#include <semaphore.h>

// constants
#define DEFAULTPAGESIZE 16;					// Default size of a page
#define DEFAULTNBPAGESINMAINMEMORY 4;		// Default number of pages in the MainMemory
#define DEFAULTSORTERTABSIZE 100;			// Default size of the sorter arrays 
#define DEBUG 0

// constants for shared memory
#define MAIN_MEMORY "/SharedMainMemory"
#define COMMAND_MAIN "/SharedCommandMain"
#define COMMAND_VIRTUAL "/SharedCommandVirtual"

 
#define SEM_SORTER "/SEMSorter"	

#define READ 0
#define WRITE 1

// structure of main command
typedef struct main_command_struct
{
	int sorterNb;		// number of sorter
	int index;			// index in the array
	int readWrite;      // is for reading or writing
	int value;          // the value
} mc_struct;

// structure of virtual command
typedef struct virtual_command_struct
{
	int page_index;     // the index of page to load

} vc_struct;

// structure of main memory
typedef struct main_memory_struct
{
	int* index;			// index in the array
	int* ring;			// used by the clock-hand
	int* buffer;		// buffer

} mm_struct;


//-------------------------------------------------------------------------------------------------
void StartMemoryProcesses(int pageSize, int memorySize, int virtualMemorySize, int sorterTabSize,
					 int pagesInMainMemory, int nbSorters, int* VMPID,  int *MMPID)
{
	pid_t 	pid;

	// VIRTUALMEMORY
	if(DEBUG) printf("Creating VirtualMemory\n");
	pid = fork();
	if (pid == -1) 
	{
			perror("VIRTUALMEMORY: fork error for");
			exit(EXIT_FAILURE);
	}
	
	if (pid == 0) // child will exec VirtualMemory
	{
		char* myArgv[6];
		char sprintf_buffer1[10], sprintf_buffer2[10], sprintf_buffer3[10], sprintf_buffer4[10];

		myArgv[0] = "VirtualMemory";
		sprintf(sprintf_buffer1, "%d", pageSize);
		myArgv[1] = sprintf_buffer1;
		sprintf (sprintf_buffer2, "%d", memorySize);
		myArgv[2] = sprintf_buffer2;
		sprintf (sprintf_buffer3, "%d", virtualMemorySize);
		myArgv[3] = sprintf_buffer3;
		sprintf (sprintf_buffer4, "%d", pagesInMainMemory);
		myArgv[4] = sprintf_buffer4;
		myArgv[5] = NULL;

		int res = execv("VirtualMemory", myArgv);
		if(DEBUG) printf("creating VirtualMemory: %s %s %s %s %s \n", myArgv[0], myArgv[1], myArgv[2], myArgv[3], myArgv[4]);
		if(res < 0)
		{
			perror("VIRTUALMEMORY: execlp error");
			exit(EXIT_FAILURE);
		}
	}
	*VMPID = pid;

	// MAINMEMORY
	if(DEBUG) printf("Creating MainMemory\n");
	pid = fork();
	if (pid == -1) 
	{
			perror("MAINMEMORY: fork error for");
			exit(EXIT_FAILURE);
	}
	if (pid == 0) // child will exec VirtualMemory
	{ 
		// child will exec MainMemory
		char* myArgv[7];
		char sprintf_buffer1[10], sprintf_buffer2[10], sprintf_buffer3[10], sprintf_buffer4[10], sprintf_buffer5[10];

		myArgv[0] = "MainMemory";
		sprintf(sprintf_buffer1, "%d", pageSize);
		myArgv[1] = sprintf_buffer1;
		sprintf (sprintf_buffer2, "%d", memorySize);
		myArgv[2] = sprintf_buffer2;
		sprintf (sprintf_buffer3, "%d", pagesInMainMemory);
		myArgv[3] = sprintf_buffer3;
		sprintf (sprintf_buffer4, "%d", sorterTabSize);
		myArgv[4] = sprintf_buffer4;
		sprintf (sprintf_buffer5, "%d", *VMPID);
		myArgv[5] = sprintf_buffer5;
		myArgv[6] = NULL;

		if(DEBUG) printf("Creating MainMemory: %s %s %s %s %s \n", myArgv[1], myArgv[2], myArgv[3], myArgv[4], myArgv[5]);

		int res = execv("MainMemory", myArgv);
		if(res < 0)
		{
			perror("MAINMEMORY: execlp error");
			exit(EXIT_FAILURE);
		}
	}
	*MMPID = pid;
}

//-------------------------------------------------------------------------------------------------
void StartSorterProcesses(int nbSorter, int sorterTabSize, int MMPid)
{
	pid_t 	pid;
	int i=0;

	for (i=0; i< nbSorter; i++)
	{
		// fork
		pid = fork();
		if (pid == -1) 
		{
				perror("SORTER: fork error for");
				exit(EXIT_FAILURE);
		}
		
		if (pid == 0) // child will exec SORTER
		{ 
		// child will exec Sorter
		char* myArgv[5];
		char sprintf_buffer1[10], sprintf_buffer2[10], sprintf_buffer3[10];

		myArgv[0] = "Sorter";
		sprintf(sprintf_buffer1, "%d", sorterTabSize);
		myArgv[1] = sprintf_buffer1;
		sprintf(sprintf_buffer2, "%d", MMPid);
		myArgv[2] = sprintf_buffer2;
		sprintf(sprintf_buffer3, "%d", i);
		myArgv[3] = sprintf_buffer3;
		myArgv[4] = NULL;

		int res = execv("Sorter", myArgv);
		if(res < 0)
			{
				perror("SORTER: execlp error");
				exit(EXIT_FAILURE);
			}
		}
	}
}

//-------------------------------------------------------------------------------------------------
void printUsage(char* name)
{
	printf("Usage: %s NbSorters [-p ] [-s ] [-m ]\n", name);
	printf("       -p:  Page Size\n");
	printf("       -m:  nb pages in MainMemory\n");
	printf("       -s:  nb of values in sorters\n");
}

//-------------------------------------------------------------------------------------------------
// Create shared main memory
void* create_main_memory(int size){
	
	printf("create/opening shared main memory... \n");

	int shm_main_fd = shm_open(MAIN_MEMORY, O_CREAT|O_RDWR, 0777);
	if (shm_main_fd == -1) {
		perror("can't create or opening shared memory for Main Memory");
		return NULL;
	}
	
	int ftruncate_return = ftruncate(shm_main_fd, size);
	void *maddr_main = mmap(NULL, size , PROT_READ | PROT_WRITE, MAP_SHARED, shm_main_fd, 0);
	
	if (ftruncate_return == -1) {
		perror("can't setup memory size for Main Memory");
	}
	if (maddr_main == MAP_FAILED) {
		perror("can't setup the mapping for Main Memory");
	}

	return maddr_main;
}

//-------------------------------------------------------------------------------------------------
// Create shared main command memory
void* create_main_command_memory(int size){
	printf("create/opening shared command main memory... \n");
	
	int shm_command_main = shm_open(COMMAND_MAIN, O_CREAT|O_RDWR, 0777);
	if (shm_command_main == -1) {
		perror("can't create or opening shared memory for Main Memory Command");
		return NULL;
	}
	
	int ftruncate_return = ftruncate(shm_command_main, size);
	void *maddr_main_command = mmap(NULL, size , PROT_READ | PROT_WRITE, MAP_SHARED, shm_command_main, 0);
	
	if (ftruncate_return == -1) {
		perror("can't setup memory size for Main Memory Command");
	}
    if (maddr_main_command == MAP_FAILED) {
		perror("setting up the mapping");
	}

	return maddr_main_command;
}

//-------------------------------------------------------------------------------------------------
// Create shared main virtual memory
void* create_virtual_command_memory(int size){
	printf("create/opening shared command virtual memory... \n");

	int shm_command_virtual = shm_open(COMMAND_VIRTUAL, O_CREAT|O_RDWR, 0777);
	if (shm_command_virtual == -1) {
		perror("can't create or opening shared memory for Virtual Memory Command");
		return NULL;
	}

	int ftruncate_return = ftruncate(shm_command_virtual, size);
	void *maddr_virtual_command = mmap(NULL, size , PROT_READ | PROT_WRITE, MAP_SHARED, shm_command_virtual, 0);
	
	if (ftruncate_return == -1) {
		perror("setting memory size");
	}
	if (maddr_virtual_command == MAP_FAILED) {
		perror("setting up the mapping");
	}
	return maddr_virtual_command;
}

//-------------------------------------------------------------------------------------------------
int main(int argc, char **argv) {
	int i;
	int pageSize = DEFAULTPAGESIZE;
	int pagesInMainMemory = DEFAULTNBPAGESINMAINMEMORY;
	int sorterTabSize = DEFAULTSORTERTABSIZE;
	int memorySize = 0;
	int virtualMemorySize = 0;
	int nbSorters = 1;

	if (argc < 2 || argc >8) 
	{
		printUsage(argv[0]);
		exit(EXIT_FAILURE);
	}

	nbSorters = atoi(argv[1]);
	i = 2;
    while (i< argc)
	{
		if (!strcmp(argv[i], "-p"))
			 pageSize = atoi(argv[i+1]);
		  else if (!strcmp(argv[i], "-m"))
			 pagesInMainMemory= atoi(argv[i+1]);
		  else if (!strcmp(argv[i], "-s"))
			 sorterTabSize = atoi(argv[i+1]);
		  else {
			 printUsage(argv[0]);
			 exit(EXIT_FAILURE);
		  }
		  i+=2;
	}

	// calculate the size of memorySize
	memorySize = (pageSize * pagesInMainMemory + 2 * pagesInMainMemory) * sizeof(int);
	virtualMemorySize = (nbSorters * sorterTabSize) * sizeof(int);
	virtualMemorySize = ((virtualMemorySize / pageSize) + sizeof(int)) * pageSize;
	
	printf("pageSize %i\n",pageSize);
	printf("pagesInMainMemory %i\n",pagesInMainMemory);
	printf("sorterTabSize %i\n",sorterTabSize);
	printf("memorySize %i\n",memorySize);
	printf("virtualMemorySize %i\n",virtualMemorySize);
	printf("nbSorters %i\n",nbSorters);

	//==============================================================================
	// Create shared main memory
	//==============================================================================
	void* maddr1 = create_main_memory(memorySize);
	if(maddr1 == NULL){
		return EXIT_FAILURE;
	}

	//==============================================================================
	// Create shared command main memory
	//==============================================================================
	int main_memory_command = sizeof(mc_struct);
	void* maddr2 = create_main_command_memory(main_memory_command);
	if(maddr2 == NULL){
		return EXIT_FAILURE;
	}

	//==============================================================================
	// Create shared command virtual memory
	//==============================================================================
	int virtual_memory_command = sizeof(vc_struct);
	void* maddr3 = create_virtual_command_memory(virtual_memory_command);
	if(maddr3 == NULL){
		return EXIT_FAILURE;
	}

	//==============================================================================
	// Start process
	//==============================================================================
	
	int main_memory_pid;
	int virtual_memory_pid;

	StartMemoryProcesses(pageSize, memorySize, virtualMemorySize, sorterTabSize, pagesInMainMemory, nbSorters, &virtual_memory_pid, &main_memory_pid);
	sleep(0.5);
	StartSorterProcesses(nbSorters, sorterTabSize, main_memory_pid);
	sleep(1);
	
	// wait for childs
	printf("MemorySimulation is waiting childs\n");
	for(i = 0; i < nbSorters; i++)
	{
		wait(NULL);
	}

	kill (main_memory_pid, SIGUSR2);
	kill (virtual_memory_pid, SIGUSR2);

	munmap(maddr1, memorySize);
	munmap(maddr2, main_memory_command);
	munmap(maddr3, virtual_memory_command);

	shm_unlink(MAIN_MEMORY); 
	shm_unlink(COMMAND_MAIN); 
	shm_unlink(COMMAND_VIRTUAL); 
	
	sem_unlink(SEM_SORTER);

	return EXIT_SUCCESS;
}