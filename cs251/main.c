#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "gw.c"

int main(int argc, char **argv) {
    GW* g;
    int population = 10;
    int nrows = 5;
    int ncols = 5;
    int rnd = 0;
    char in[200];
    int i;
    int j;
    int x;
    int r;
    int c;
    int* members;
    int numMembers;
    int index;

    // Check command line arguments
	for (int i = 0; i < argc; i++) {
		if (strcmp(argv[i], "-N") == 0) {
			population = atoi(argv[i+1]);
		}
		if (strcmp(argv[i], "-R") == 0) {
			nrows = atoi(argv[i+1]);
		}
		if (strcmp(argv[i], "-C") == 0) {
			ncols = atoi(argv[i+1]);
		}
		if (strcmp(argv[i], "-rand") == 0) {
			rnd = 1;
		}
	}

	// Create gridworld
	g = gw_build(nrows, ncols, population, rnd);

	// Menu
	while(1) {
		printf(">");
		fgets(in, 200, stdin);

		// Check for commands without parameters
		// Quit
		if (strcmp(in, "quit\n") == 0) {
			break; // Exit while loop
			return 0; // Quit program
		}
		// Population
		if (strcmp(in, "population\n") == 0) {
			printf("\t%d\n", gw_total_pop(g));
			continue;
		}

		// Check commands
		// Members
		if(sscanf(in, "members %d %d", &i, &j) == 2) {
			members = gw_members(g, i, j, &numMembers);
			printf("\t[ ");
			for (index = 0; index < numMembers; index++) {
				printf("%d ", members[index]);
			}
			printf("]\n");
		}
		
		// Population
		else if (sscanf(in, "population %d %d", &i, &j) == 2) {
			int ret = gw_district_pop(g, i, j);
			if (ret == -1) {
				printf("\tError: District does not exists\n");
			} else {
				printf("\t%d\n", ret);
			}
		}
		
		// Move
		else if (sscanf(in, "move %d %d %d", &x, &i, &j) == 3) {
			printf("\t%d\n", gw_move(g, x, i, j));
		}
		
		// Find
		else if (sscanf(in, "find %d", &x) == 1) {
			if (gw_find(g, x, &r, &c)) {
				printf("\tLocated at: (%d, %d)\n", r, c);
			} else {
				printf("\tError: Either does not exist or is not alive.\n");
			}
		}
		
		// Kill
		else if (sscanf(in, "kill %d", &x) == 1) {
			printf("\t%d\n", gw_kill(g, x));
		}
		
		// Create
		else if (sscanf(in, "create %d %d", &i, &j) == 2) {
			int ret = gw_create(g, i, j);
			if (ret == -1) {
				printf("\tError: Invalid district\n");
			} else {
				printf("\tID: %d\n", ret);
			}
		}
		
		// Invalid
		else {
			printf("\tNo such command.\n");
		}
	} // End menu loop

    return 0;
}
