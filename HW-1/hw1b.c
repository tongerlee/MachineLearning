#include <stdio.h>
#include <limits.h>
#include <stdlib.h>

int
main (int argc, char *argv[]){
  if(argc < 2){
    printf("Invalid Input, Need a input file\n");
    exit(1);
  }
  else if (argc > 2) {
    printf("Invalid Input, no more than one input file\n");
    exit(1);
  }
  else {
    FILE *fp = fopen(argv[1], "r");
    if(fp == NULL) {
      printf("Cannot open file\n");
      exit(1);
    }
    char **buffer = (char **)malloc(1024 * sizeof(char *));
    int i;
    int printCount;
    int j;
    for (i = 0; i < 1024; i++){
      buffer[i] = (char *)malloc (LINE_MAX * sizeof(char));
    }
    int count = 0;
    while(fgets(buffer[count], LINE_MAX, fp) != NULL) {
      count++;
    }
    for (printCount = count-1; printCount >=0; printCount--){
        fputs(buffer[printCount], stdout);
    }
    fclose(fp);
    for (j = 0; i < 1024; i++){
      free(buffer[i]);
    }
    free(buffer);
    return 0;
  }
}
      
