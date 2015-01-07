/*
 =============================================
 Name        : MemorySimulation.c
 Author      : 
 Version     : 
 Description : Process that launches the other processes and handle the simulation
 =============================================
 */

#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// constants
#define DEFAULTPAGESIZE 16;			// Default size of a page
#define DEFAULTNBPAGESINMAINMEMORY 4;		// Default number of pages in the MainMemory
#define DEFAULTSORTERTABSIZE 100;	// Default size of the sorter arrays 
#define DEBUG 0

// creation of the VIRTUALMEMORY and MAINMEMORY processes
// return the VirtualMemory and MainMemory PIDs
void StartMemoryProcesses(int pageSize, int memorySize, int virtualMemorySize, int sorterTabSize, int pagesInMainMemory, int nbSorters, int* VMPID,  int *MMPID)
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

// 
// create SORTER processes
//
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

void printUsage(char* name)
{
	printf("Usage: %s NbSorters [-p ] [-s ] [-m ]\n", name);
	printf("       -p:  Page Size\n");
	printf("       -m:  nb pages in MainMemory\n");
	printf("       -s:  nb of values in sorters\n");
}

//
// main
//
int main(int argc, char **argv) 
{
	char charToPrint;
	int nbOfChars;	
	int semValue = 0;
	int i;

	//////////////////////////
 	// define parameters
	int pageSize = DEFAULTPAGESIZE;
	int pagesInMainMemory = DEFAULTNBPAGESINMAINMEMORY;
	int sorterTabSize = DEFAULTSORTERTABSIZE;
	int memorySize = 0;
	int virtualMemorySize = 0;
	int nbSorters = 1;

	if(DEBUG) printf("argc %d  argv[0]: %s\n", argc, argv[0]);
	
	if (argc < 2 || argc >8) 
	{
		printUsage(argv[0]);
		exit(EXIT_FAILURE);
	}

	nbSorters = atoi(argv[1]);
	i=2;
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

	//////////////////////////
    // TO BE COMPLETED....

	

	return EXIT_SUCCESS;
}
