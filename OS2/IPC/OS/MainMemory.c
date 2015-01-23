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

#define MAIN_MEMORY "/SharedMainMemory"
#define COMMAND_MAIN "/SharedCommandMain"
#define COMMAND_VIRTUAL "/SharedCommandVirtual"

#define SEM_MAIN "/SharedSEMMain"  
#define SEM_VIRT "/SharedSEMVirt"
#define SEM_SORTER "/SEMSorter" 

#define READ 0
#define WRITE 1

// structure of main command
typedef struct main_command_struct
{
    int sorterNb;       // number of sorter
    int index;          // index in the array
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
    int* index;         // index in the array
    int* ring;          // used by the clock-hand
    int* buffer;        // buffer

} mm_struct;

sem_t *sem_main;          
sem_t *sem_virt; 

mc_struct* main_command;
vc_struct* virtual_command;
mm_struct memory;

int running;         
int sorterTabSize;           
int pageSize;                  
int pagesInMainMemory;              
int vm_pid;
int memorySize;                  

//-------------------------------------------------------------------------------------------------
void get_virtual_sem()
{
    if( sem_wait(sem_virt) == -1 ) {
        perror("was waiting for virtual semaphore");
        exit(EXIT_FAILURE);
    }
}

//-------------------------------------------------------------------------------------------------
void release_virtual_sem()
{
    if( sem_post(sem_virt) == -1) {
        perror("was releasing virtual semaphore...");
        exit(EXIT_FAILURE);
    }
}

//-------------------------------------------------------------------------------------------------
void get_main_sem()
{
    if( sem_wait(sem_main) == -1 ) {
        perror("was waiting for main semaphore");
        exit(EXIT_FAILURE);
    }
}

//-------------------------------------------------------------------------------------------------
void release_main_sem()
{
    if( sem_post(sem_main) == -1) {
        perror("was releasing main semaphore...");
        exit(EXIT_FAILURE);
    }
}

//-------------------------------------------------------------------------------------------------
static void sig_usr(int signo){

    if (signo == SIGUSR1){
        
        int indexInPage = (main_command->sorterNb * sorterTabSize + main_command->index) % pageSize;
        int pageIndex = (main_command->sorterNb * sorterTabSize + main_command->index) / pageSize;
        int i=0;

        while (i < pagesInMainMemory && memory.index[i] != pageIndex) i++;
        
        if (i == pagesInMainMemory){
            virtual_command->page_index = pageIndex;
            kill (vm_pid, SIGUSR1) ;
            //getVirtualMemorySemaphore(); 
            i = 0;
            while (i < pagesInMainMemory && memory.index[i] != pageIndex) i++;
        }

        if(main_command->readWrite == READ){

            int *memAdress = memory.buffer + i * pageSize + indexInPage;
            memcpy ( &main_command->value, memAdress, sizeof(int));

        }else{
            int *memAdress = memory.buffer + i * pageSize + indexInPage;
            memcpy ( memAdress, &main_command->value, sizeof(int));

        }
        memory.ring[i] = 1;
        
        release_main_sem();

    }else if (signo == SIGUSR2) {
        printf("MainMemory received SIGUSR2\n");
        running = 0;
    }
    else{
        printf("received signal %d\n", signo);
    }
}


//-------------------------------------------------------------------------------------------------
// Create shared main memory
void create_main_memory(){

    int shm_main_fd = shm_open(MAIN_MEMORY, O_CREAT|O_RDWR, 0777);
    if (shm_main_fd == -1) {
        perror("can't create or opening shared memory for Main Memory");
        exit(EXIT_FAILURE);
    }

    memory.index = (int*) mmap(NULL, memorySize , PROT_READ | PROT_WRITE, MAP_SHARED, shm_main_fd, 0);
    memory.ring = memory.index + pagesInMainMemory * sizeof(int);
    memory.buffer = memory.index + 2 * pagesInMainMemory * sizeof(int);
    
    if (memory.index == MAP_FAILED) {
        perror("can't setup the mapping for Main Memory");
    }
}

//-------------------------------------------------------------------------------------------------
// Create shared main command memory
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
// Create shared main virtual memory
void create_virtual_command_memory(){
    printf("create/opening shared command virtual memory... \n");

    int shm_command_virtual = shm_open(COMMAND_VIRTUAL, O_CREAT|O_RDWR, 0777);
    if (shm_command_virtual == -1) {
        perror("can't create or opening shared memory for Virtual Memory Command");
        exit(EXIT_FAILURE);
    }
    int size = sizeof(vc_struct);
    virtual_command =(vc_struct*) mmap(NULL, size , PROT_READ | PROT_WRITE, MAP_SHARED, shm_command_virtual, 0);
    if (virtual_command == MAP_FAILED) {
        perror("setting up the mapping");
    }
}

//-------------------------------------------------------------------------------------------------
int main (int argc, char *argv[]){

    if (argc !=6) 
    {
        printf("Usage: MainMemory pageSize memorySize pagesInMainMemory sorterTabSize vm_pid\n");
        exit(EXIT_FAILURE);
    }

    int i = argc;
    vm_pid = atoi(argv[--i]);
    sorterTabSize = atoi(argv[--i]);
    pagesInMainMemory = atoi(argv[--i]);
    memorySize = atoi(argv[--i]);
    pageSize = atoi(argv[--i]);
    
    // prepare to catch the signal
    if (signal(SIGUSR1, sig_usr) == SIG_ERR)
    {
        printf("can't catch SIGUSR1\n");
        exit(EXIT_FAILURE) ;
    }
    if (signal(SIGUSR2, sig_usr) == SIG_ERR)
    {
        printf("can't catch SIGUSR2\n");
        exit(EXIT_FAILURE) ;
    }
    
    if ((sem_main = sem_open(SEM_MAIN, O_CREAT, 0666, 0)) == SEM_FAILED ) {
       perror("can't open SEM_MAIN");
       exit(EXIT_FAILURE);
    }
    if ((sem_virt = sem_open(SEM_VIRT, O_CREAT, 0666, 0)) == SEM_FAILED ) {
       perror("can't open SEM_VIRT");
       exit(EXIT_FAILURE);
    }


    //==============================================================================
    // Create shared main memory
    //==============================================================================
    create_main_memory();


    //==============================================================================
    // Create shared command main memory
    //==============================================================================
    create_main_command_memory();


    //==============================================================================
    // Create shared command virtual memory
    //==============================================================================
    create_virtual_command_memory();

    

    //==============================================================================
    // waiting...
    //==============================================================================
    
    running =1;
    while(running)
    {
        sleep(1);
    }
    
    sem_close(sem_main);
    sem_close(sem_virt);
    sem_unlink(SEM_MAIN);


    munmap(memory.index, memorySize);
    munmap(main_command, sizeof(mc_struct));
    munmap(virtual_command, sizeof(vc_struct));

    return EXIT_SUCCESS;   
}