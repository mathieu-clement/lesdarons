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

#define SEM_VIRT "/SharedSEMVirt"

#define READ 0
#define WRITE 1

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

sem_t *sem_virt; 

vc_struct* virtual_command;
mm_struct memory;

int running;         
int sorterTabSize;           
int pageSize;                  
int pagesInMainMemory;              
int vm_pid;
int memorySize;
int virtualMemorySize; 
int current_index; 

int* virtual_memory; 

//-------------------------------------------------------------------------------------------------
void release_virtual_sem()
{
    if( sem_post(sem_virt) == -1) {
        perror("was releasing virtual semaphore...");
        exit(EXIT_FAILURE);
    }
}

//-------------------------------------------------------------------------------------------------
int index_page_for_switch(){
    int i = current_index;

    while(memory.ring[i] == 1){
        memory.ring[i] = 0;
        i++;
        i %= pagesInMainMemory;
    }
    current_index = (i + 1) % pagesInMainMemory;
    return i;
}

//-------------------------------------------------------------------------------------------------
void load_page(int page_index, int main_index){

    int* virtual_ptr = virtual_memory + page_index * pageSize;
    int* main_ptr = memory.buffer + main_index * pageSize;

    memcpy (main_ptr, virtual_ptr, pageSize * sizeof(int));
    memory.ring[main_index] = 0;
    memory.index[main_index] = page_index;   
}

//-------------------------------------------------------------------------------------------------
void replace_page(int page_index, int main_index){

    int* virtual_ptr = virtual_memory + page_index * pageSize;
    int* main_ptr = memory.buffer + main_index * pageSize;
    memcpy ( virtual_ptr, main_ptr, pageSize * sizeof(int));
}


//-------------------------------------------------------------------------------------------------
static void sig_usr(int signo){
    if (signo == SIGUSR1){
        
        int index_to_replace = index_page_for_switch();
        replace_page(memory.index[index_to_replace], index_to_replace);
        load_page(virtual_command->page_index, index_to_replace);
            
        release_virtual_sem();

    }else if (signo == SIGUSR2){
        printf("VirtualMemory received SIGUSR2\n");
        running=0;
    }else{
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

    if(argc != 5){
        perror("Usage : pageSize, memorySize, virtualMemorySize, pagesInMainMemory");
        exit(EXIT_FAILURE);
    }

    // get argument values
    int i = argc;
    pagesInMainMemory = atoi(argv[--i]);
    virtualMemorySize = atoi(argv[--i]);
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

    if ((sem_virt = sem_open(SEM_VIRT, O_CREAT, 0666, 0)) == SEM_FAILED ) {
       perror("can't open SEM_VIRT");
       exit(EXIT_FAILURE);
    } 
    
    //==============================================================================
    // Create shared main memory
    //==============================================================================
    create_main_memory();


    //==============================================================================
    // Create shared command virtual memory
    //==============================================================================
    create_virtual_command_memory();

    
    //==============================================================================
    // Initialize virtual memory
    //==============================================================================
    virtual_memory = malloc(virtualMemorySize);
    memset(virtual_memory, 0, virtualMemorySize);
    
    if(virtual_memory == 0)
    {
        printf("Can't allocate virtual memory\n");
        exit(EXIT_FAILURE);
    }

    for (i = 0; i < pagesInMainMemory; i++) load_page(i, i);
    current_index = 0;

    //==============================================================================
    // wait ....
    //==============================================================================
    running = 1;
    while(running) sleep(1);
    
    sem_close(sem_virt);
    sem_unlink(SEM_VIRT);

    // release shared memory
    munmap(memory.index, memorySize);
    munmap(virtual_command, sizeof(vc_struct)); 

    free(virtual_memory);
    
    return EXIT_SUCCESS;   
}