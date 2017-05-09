#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>

struct members_struct {
	int id;
	struct members_struct* prev;
	struct members_struct* next;
};

struct district_struct {
	int pop;
	struct members_struct* members;
};

struct people_struct {
	int row;
	int col;
	bool status;
	struct members_struct* districtPtr;
};

struct availIds_struct {
	int id;
	struct availIds_struct* next;
};

typedef struct gw_struct {
	struct district_struct** district;
	struct people_struct* people;
	struct availIds_struct* availIds;
	int peopleArraySize;
	int totalPop;
	int row;
	int col;
} GW;

/** Constructs main GridWorld structure.
 * @param	nrows	number of rows
 * @param	ncols	number of columns
 * @param	pop		population
 * @param	rnd		random flag from command line
 * @return	pointer to a GW
 * @runtime	O(CR + N)
 */
GW* gw_build(int nrows, int ncols, int pop, int rnd) {
	GW* Gridworld;
	int i;
	int j;
	int ri;
	int rj;

	// Check randomness flag
	if (rnd == 0) {
		ri = 0;
		rj = 0;
	} else {
		srand(time(NULL));
		ri = rand()%nrows;
		rj = rand()%ncols;
	}

	Gridworld = malloc(sizeof(GW));		// Allocate memory for Gridworld structure
	Gridworld->totalPop = pop;			// Set total population
	Gridworld->peopleArraySize = pop;	// Set array book-keeping
	Gridworld->row = nrows;				// Set rows
	Gridworld->col = ncols;				// Set columns

	// Initialize stack of available IDs
	Gridworld->availIds = NULL;

	// Allocate memory for every district
	Gridworld->district = malloc(sizeof(struct district_struct*) * nrows);
	for (i = 0; i < nrows; i++) {
		Gridworld->district[i] = malloc((sizeof(struct district_struct) * ncols));

		// Initialize each district to be empty
		for (j = 0; j < ncols; j++) {
			Gridworld->district[i][j].pop = 0;			// Population of 0
			Gridworld->district[i][j].members = NULL;	// Empty population list
		}
	}

	// Initialize population inside Gridworld structure
	Gridworld->people = malloc(sizeof(struct people_struct) * pop); // Allocate memory for input population
	for (i = 0; i < pop; i++) {
		// Add person to members of district linked list
		struct members_struct* member = malloc(sizeof(struct members_struct)); // Allocate memory for new member of district
		member->id = i;

		// Is the list empty?
		if (Gridworld->district[ri][rj].members == NULL) {
			member->prev = NULL;
			member->next = NULL;
			Gridworld->district[ri][rj].members = member;
		}
		// Otherwise the member list has at least one node
		else {
			member->prev = NULL;
			Gridworld->district[ri][rj].members->prev = member;
			member->next = Gridworld->district[ri][rj].members;
			Gridworld->district[ri][rj].members = member; // Make new member the head pointer
		}

		// Initialize this persons values
		Gridworld->people[i].row = ri;
		Gridworld->people[i].col = rj;
		Gridworld->people[i].status = 1;
		Gridworld->people[i].districtPtr = member;

		// Update district population
		Gridworld->district[ri][rj].pop += 1;
	}

	return Gridworld;
}

/** Check and return members from a given district
 * @param	g		GridWorld structure
 * @param	i		District row
 * @param	j		District column
 * @param	n		returns length of integer array if district is valid
 * @return	integer array of people living in the given district. if district is invalid, return NULL.
 * @runtime	O(N_ij)
 */
int* gw_members(GW* g, int i, int j, int* n) {
	// Error checking
	if (i >= g->row && j >= g->col) {
		return NULL;
	}

	// Local variables
	int index = 0;
	int* arr = malloc(sizeof(int) * g->district[i][j].pop); // Allocate array
	struct members_struct* membersPtr; // Temporary local pointer

	// Begin function
	*n = g->district[i][j].pop; // Number of people living in this district;
	membersPtr = g->district[i][j].members;

	// Copy members into new array
	while (membersPtr != NULL) {
		arr[index] = membersPtr->id;
		membersPtr = membersPtr->next;
		index++;
    }

	return arr;
}

/** Get population from given district.
 * @param	g		GridWorld structure
 * @param	i		District row
 * @param	j		District column
 * @return	number of people living in given district. if district is invalid, return -1.
 * @runtime	O(1)
 */
int gw_district_pop(GW* g, int i, int j) {
	// Error checking
	if (i >= g->row || j >= g->col) {
		return -1;
	}

	return g->district[i][j].pop;
}

/** Get total population
 * @param	g		GridWorld structure
 * @return	total population.
 * @runtime	O(1)
 */
int gw_total_pop(GW* g) {
	return g->totalPop;
}

/** Move person x from current district to given district
 * @param	g		GridWorld structure
 * @param	x		Person ID
 * @param	i		New district row
 * @param	j		New district column
 * @return	1 if success. 0 if not.
 * @runtime	O(1)
 */
int gw_move(GW* g, int x, int i, int j) {
	// Verify if person exists
	if (x >= g->totalPop) {
		return 0; // Did not exist
	}

	// Verify district existence
	if (i >= g->row && j >= g->col) {
		return 0;
	}

	// Verify if person is alive
	if (g->people[x].status == 0) {
		return 0; // Was not alive
	}

	// Does this person already live here?
	if (g->people[x].row == i && g->people[x].col == j) {
		return 1;
	}

	g->district[g->people[x].row][g->people[x].col].pop -= 1; // Update old district population
	g->district[i][j].pop += 1; // Update new district population

	// Remove from member list of old district
	if (g->people[x].districtPtr->prev == NULL && g->people[x].districtPtr->next == NULL)
		g->district[g->people[x].row][g->people[x].col].members = NULL; // NULL out the member list
	else if (g->people[x].districtPtr->prev == NULL)
		g->people[x].districtPtr->next->prev = NULL;
	else if (g->people[x].districtPtr->next == NULL)
		g->people[x].districtPtr->prev->next = NULL;
	else {
		g->people[x].districtPtr->prev->next = g->people[x].districtPtr->next;
		g->people[x].districtPtr->next->prev = g->people[x].districtPtr->prev;
	}

	// Update person location
	g->people[x].row = i;
	g->people[x].col = j;

	// Add to member list of new district
	g->people[x].districtPtr->prev = NULL;
	g->people[x].districtPtr->next = g->district[i][j].members;
	g->district[i][j].members = g->people[x].districtPtr; // Make new member of district the head pointer

	return 1;
}

/** Check if person x exists.
 * @param	g		GridWorld structure
 * @param	x		Person ID
 * @param	r		District row of person x
 * @param	c		District column of person x
 * @return	1 if success. 0 if not.
 * @runtime	O(1)
 */
int gw_find(GW* g, int x, int* r, int* c) {
	// Verify if person exists
	if (x >= g->totalPop) {
		return 0; // Does not exist
	}

	// Verify if alive
	if (g->people[x].status == 1) {
		*r = g->people[x].row;
		*c = g->people[x].col;
		return 1;
	} else {
		return 0; // Was not alive
	}
}

/** If person x exists, remove them from the world.
 * @param	g		GridWorld structure
 * @param	x		Person ID
 * @return	1 if success. 0 if not.
 * @runtime	O(1)
 */
int gw_kill(GW* g, int x) {
	// Verify if person exists (check ID by total population)
	if (x >= g->totalPop) {
		return 0;
	}

	// Verify if dead
	if (g->people[x].status == 0) {
		return 0; // Was dead
	}

	g->people[x].status = 0; // Dead
	g->totalPop -= 1; // Update gridworld population
	g->district[g->people[x].row][g->people[x].col].pop -= 1; // Update district population

	// Remove from member list of district
	if (g->people[x].districtPtr->prev == NULL && g->people[x].districtPtr->next == NULL)
		g->district[g->people[x].row][g->people[x].col].members = NULL; // NULL out the member list
	else if (g->people[x].districtPtr->prev == NULL) {
		g->district[g->people[x].row][g->people[x].col].members = g->people[x].districtPtr->next;
		g->people[x].districtPtr->next->prev = NULL;
	}
	else if (g->people[x].districtPtr->next == NULL)
		g->people[x].districtPtr->prev->next = NULL;
	else {
		g->people[x].districtPtr->prev->next = g->people[x].districtPtr->next;
		g->people[x].districtPtr->next->prev = g->people[x].districtPtr->prev;
	}

	// Add to list of available IDs
	struct availIds_struct* dead = malloc(sizeof(struct availIds_struct));
	dead->id = x;
	if (g->availIds == NULL) {
		dead->next = NULL;
		g->availIds = dead;
	} else {
		dead->next = g->availIds; // Append list to newly created node
		g->availIds = dead; // Make this ID the head pointer
	}

	return 1;
}


/** Create new person in given district.
 * @param	g		GridWorld structure
 * @param	i		Birth district row
 * @param	j		Birth district column
 * @return	ID of newly created person. -1 if failure.
 * @runtime	O(1)
 */
int gw_create(GW* g, int i, int j) {
	// Verify district existence
	if (i >= g->row && j >= g->col) {
		return -1;
	}

	// Local variables
	int id;

	// Assign new ID if no IDs are available to use
	if (g->availIds == NULL) {
		id = g->totalPop;

		// Add person to members of district linked list
		struct members_struct* member = malloc(sizeof(struct members_struct)); // Allocate memory for new member of district
		member->id = id;

		// Is the list empty?
		if (g->district[i][j].members == NULL) {
			member->prev = NULL;
			member->next = NULL;
			g->district[i][j].members = member;
		}
		// Otherwise the member list has at least one node
		else {
			member->prev = NULL;
			g->district[i][j].members->prev = member;
			member->next = g->district[i][j].members;
			g->district[i][j].members = member; // Make new member the head pointer
		}

		// Double people array if full
		if (g->peopleArraySize == g->totalPop) {
			struct people_struct* newPeople = malloc(sizeof(struct people_struct) * 2 * g->peopleArraySize);
			int i;

			// Copy old array data to new array
			for (i = 0; i < g->peopleArraySize; i++) {
				newPeople[i] = g->people[i];
			}

			g->peopleArraySize *= 2; // Update array size;

			free(g->people);
			g->people = newPeople;
		}

		g->people[id].districtPtr = member; // Add their member list pointer
	}
	// Otherwise use an available ID
	else {
		id = g->availIds->id;

		// Pop available ID stack
		struct availIds_struct* removeId = g->availIds; // Temporary copy of top of stack
		g->availIds = g->availIds->next; // Point top of stack to next
		free(removeId); // Remove old top of stack

		// Update their member list pointer
		// Is the list empty?
		if (g->district[i][j].members == NULL) {
			g->people[id].districtPtr->prev = NULL;
			g->people[id].districtPtr->next = NULL;
			g->district[i][j].members = g->people[id].districtPtr;
		}
		// Otherwise the member list has at least one node
		else {
			g->people[id].districtPtr->prev = NULL;
			g->district[i][j].members->prev = g->people[id].districtPtr;
			g->people[id].districtPtr->next = g->district[i][j].members;
			g->district[i][j].members = g->people[id].districtPtr; // Make new member the head pointer
		}
	}

	// Initialize this persons values
	g->people[id].row = i;
	g->people[id].col = j;
	g->people[id].status = 1;

	g->district[i][j].pop += 1;// Update district population
	g->totalPop += 1; // Update gridworld population

	return id;
}


/** Free all data from given GridWorld.
 * @param	g		GridWorld structure
 */
void gw_free(GW* g) {
	int i;
	int j;

	// Free available IDs
	if (g->availIds != NULL) {
		while (g->availIds != NULL) {
			struct availIds_struct* remId = g->availIds;
			g->availIds = g->availIds->next;
			free(remId);
		}
	}

	// Free people array
	free(g->people);

	// Free members
	for (i = 0; i < g->row; i++) {
		for (j = 0; j < g->col; j++) {
			while (g->district[i][j].members != NULL) {
				struct members_struct* remMem = g->district[i][j].members;
				g->district[i][j].members = g->district[i][j].members->next;
				free(remMem);
			}
		}
	}

	free(g->district); // Free district

	free(g);
}
