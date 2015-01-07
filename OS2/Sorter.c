/*
 =============================================
 Name        : Sorter.c
 Author      : 
 Version     :
 Description : Process that sort an array using the quicksort algorithm
 =============================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>


// get and set values
int getArrayValue(int index)
{
	// TO BE COMPLETED....
}

void setArrayValue(int index, int value)
{
	// TO BE COMPLETED....
}

/* quick sort */
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

//////////////////////////
// main  
int main(int argc, char **argv) {


	//////////////////////////
    // TO BE COMPLETED....

	
	///////////////////////////////////////
	// fill	the vector with random numbers
	int i;
	srand(getpid()); 	//  rand initialisation
	for(i=0; i< sorterTabSize; i++)
	{
		setArrayValue(i, rand() % 100);
		printf("%d: vector[%d]=%d\n", sorterId, i, getArrayValue(i));
	}

	///////////////////
	// sort	the vector
	quicksort(0,sorterTabSize-1);
	printf("\n\n");
	int val;
	int nbError=0;
	
	/////////////////////////////
	// Display the vector content
	for(i=0; i< sorterTabSize; i++)
	{
		val=getArrayValue(i);
		printf("     %d: vector[%d]=%d", sorterId, i, val);
		if(i>0)
		{
			if( val < getArrayValue(i-1) )
			{
				printf("           ERROR !!! \n");
				nbError++;
			}
			else
			{
				printf("\n");
			}
		}
	}
	if(nbError>0)
		printf("Sorter %d produced %d errors\n", sorterId, nbError);
	else
		printf("Sorter %d worked successfully\n", sorterId);

	return 0;
}
