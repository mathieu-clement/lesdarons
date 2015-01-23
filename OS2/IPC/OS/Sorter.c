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

#define COMMAND_MAIN "/SharedCommandMain"

#define SEM_MAIN "/SharedSEMMain"	
#define SEM_VIRT "/SharedSEMVirt"
#define SEM_SORTER "/SEMSorter"			

#define READ 0
#define WRITE 1

typedef struct main_command_struct
{
    int sorterNb;       // number of sorter
    int index;          // index in the array
    int readWrite;      // is for reading or writing
    int value;          // the value
} mc_struct;

mc_struct* main_command;

int id_sorter;
int mm_pid;

sem_t *sem_sorter; 
sem_t *sem_main;

//-------------------------------------------------------------------------------------------------
void get_sorter_sem(){

	if( sem_wait(sem_sorter) == -1 ) {
		perror("was waiting for sorter semaphore");
		exit(EXIT_FAILURE);
	}
}

//-------------------------------------------------------------------------------------------------
void release_sorter_sem()
{
	if( sem_post(sem_sorter) == -1) {
		perror("was realease sorter semaphore");
		exit(EXIT_FAILURE);
	}
}

//-------------------------------------------------------------------------------------------------
void get_memory_sem()
{
	if( sem_wait(sem_main) == -1 ) {
		perror("was waiting for main semaphore");
		exit(EXIT_FAILURE);
	}
}

//-------------------------------------------------------------------------------------------------
void release_memory_sem()
{
	if( sem_post(sem_main) == -1) {
		perror("was realease sorter main");
		exit(EXIT_FAILURE);
	}
}					

//-------------------------------------------------------------------------------------------------
int getArrayValue(int index){
	get_sorter_sem();

	main_command->sorterNb = id_sorter;
	main_command->index = index;
	main_command->readWrite = READ;

	kill (mm_pid, SIGUSR1) ; 

	get_memory_sem();
	
	int value = main_command->value;
	//printf("=== Sorter [%d] get %d at index %d ===\n", id_sorter, value, main_command->index);
	
	release_sorter_sem();
	return value;
}

//-------------------------------------------------------------------------------------------------
void setArrayValue(int index, int value){
	get_sorter_sem();

	
	main_command->sorterNb = id_sorter;
	main_command->index = index;
	main_command->readWrite = WRITE;
	main_command->value = value;

	kill (mm_pid, SIGUSR1);
	
	get_memory_sem();
	release_sorter_sem();
}

//-------------------------------------------------------------------------------------------------
void quicksort(int start, int end){
	int pivot, start_tmp, end_tmp; 	// store pivot
	start_tmp  = start; 			// work variable  (and keep start value for split)
	end_tmp = end;					// work variable  (and keep end value for split)
	pivot = getArrayValue(start_tmp);
	while(start_tmp < end_tmp)
	{
		while((getArrayValue(end_tmp) >= pivot) && (start_tmp < end_tmp))
			end_tmp--;
		if (start_tmp != end_tmp)
		{
			setArrayValue(start_tmp, getArrayValue(end_tmp));
			start_tmp++;
		}
		while ((getArrayValue(start_tmp) <= pivot) && (start_tmp < end_tmp))
			start_tmp++;
		if (start_tmp != end_tmp)
		{
			setArrayValue(end_tmp, getArrayValue(start_tmp));
			end_tmp--;
		}
	}
	setArrayValue(start_tmp, pivot);
	pivot = start_tmp;
	if(start < pivot)
		quicksort(start, pivot-1);
	if(end > pivot)
		quicksort(pivot+1, end);
}

//-------------------------------------------------------------------------------------------------
void create_main_command_memory(){
    int shm_command_main = shm_open(COMMAND_MAIN, O_CREAT|O_RDWR, 0777);
    if (shm_command_main == -1) {
        perror("can't create or opening shared memory for Main Memory Command");
        exit(EXIT_FAILURE);
    }

    int size = sizeof(mc_struct);
    main_command =(mc_struct*) mmap(NULL, size , PROT_READ | PROT_WRITE, MAP_SHARED, shm_command_main, 0);
    if (main_command == MAP_FAILED) {
        perror("setting up the mapping");
    }
}

//-------------------------------------------------------------------------------------------------
int main(int argc, char **argv) {
	if (argc != 4) {
		printf("Usage: sorterTabSize mm_pid id_sorter\n");
		exit(EXIT_FAILURE);
	}

	int i = argc;
	
	id_sorter = atoi(argv[--i]);
	mm_pid = atoi(argv[--i]);
	int sorterTabSize = atoi(argv[--i]);
	
	//==============================================================================
    // Open semaphore
    //==============================================================================
			

	if ((sem_main = sem_open(SEM_MAIN, O_CREAT, 0666, 0)) == SEM_FAILED ) {
	   perror("can't open memory semaphore");
	   exit(EXIT_FAILURE);
	}		
	if ((sem_sorter = sem_open(SEM_SORTER, O_CREAT, 0666, 1)) == SEM_FAILED ) {
	   perror("can't open sorter semaphore");
	   exit(EXIT_FAILURE);
	}
	
	//==============================================================================
    // Create shared command main memory
    //==============================================================================
	
	create_main_command_memory();
	
	//==============================================================================
    // fill the vector with random numbers
    //==============================================================================
	
	srand(getpid()); 	//  rand initialisation
	for(i=0; i< sorterTabSize; i++)
	{
		setArrayValue(i, rand() % 100);
		printf("id [%d] : vector[%d]=%d\n", id_sorter, i, getArrayValue(i));
	}

	//==============================================================================
    // sort the vector
    //==============================================================================
	quicksort(0,sorterTabSize-1);
	printf("\n");
	int val;
	int nbError=0;
	
    //==============================================================================
    // display vector content
    //==============================================================================
	for(i=0; i< sorterTabSize; i++)
	{
		val=getArrayValue(i);
		printf("id [%d] : vector[%d]=%d\n", id_sorter, i, val);
		if(i > 0)
		{
			if( val < getArrayValue(i-1) )
			{
				printf("           ERROR !!! \n");
				nbError++;
			}
		}
	}
	if(nbError>0)
		printf("Sorter %d produced %d errors\n", id_sorter, nbError);
	else
		printf("Sorter %d worked successfully\n", id_sorter);

	sem_close(sem_sorter);
	sem_close(sem_main);

	// release shared memory
	munmap(main_command, sizeof(mc_struct));
	
	return EXIT_SUCCESS;
}
