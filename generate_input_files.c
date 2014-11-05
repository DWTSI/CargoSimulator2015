#include "simlib/simlib.h"
#include <math.h>

/*  EVENT_LAND1, EVENT_LAND2, and EVENT_LAND3
	must stay as 1, 2, and 3, respectively
	or else schedule_input_list will
	schedule the wrong events.  */
#define EVENT_LAND1         1  /*plane 1 lands*/
#define EVENT_LAND2         2  /*plane 2 lands*/
#define EVENT_LAND3         3  /*plane 3 lands*/
#define EVENT_TAKEOFF       4  /*plane takes off*/
#define EVENT_STORM_START   5  /*storm starts*/
#define EVENT_STORM_END     6  /*storm ends*/

/*  not for this simulation, was originally for a simpler
    test simulation I was going to write to become more
    familiar with simlib. */
#define EVENT_REPAIR  9  /*plane is repaired*/


#define TIME_HOUR    1
#define TIME_DAY    24
#define TIME_YEAR 8760

/*Relative frequencies of the plane types*/
#define FREQ_PLANE1 0.25
#define FREQ_PLANE2 0.25
#define FREQ_PLANE3 0.5

#define TIME_LAND_FREQ     11  /*how frequently planes land*/
#define TIME_LAND_FREQ_VAR  2  /*variation in plane landing frequency*/
#define TIME_STORM_DUR      4  /*average duration of a storm*/
#define TIME_STORM_VAR      2  /*variation in storm duration*/

/* 	This code is irrelevant to the project.
 	I was going to write a simpler simulation
 	to get familiar with simlib, and these
 	variables were going to be used for that
	one.  */
#define TIME_LANDED        10  /*amount of time plane is on ground before taking off*/
#define TIME_LAND_VAR       2  /*varation in the time plane is landed (plus-or-minus this value)*/
#define TIME_REPAIR        15  /*amount of time to repair plane*/
#define TIME_REPAIR_VAR     3  /*varation in the time taken to fix plane (plus-or-minus this value)*/

#define STREAM_INTERARRIVAL   1
#define STREAM_PLANE_TYPE     2
#define STREAM_LOADING        3
#define STREAM_STORM_DURATION 4
#define STREAM_STORM_TIME     5

typedef enum { false, true } bool;

FILE *plane_list, *storm_list;

void test_event_insert();
void generate_input_files(void);
void generate_landing_list(FILE*, float);
void generate_storm_list(FILE*, float);
void schedule_input_list(FILE*);
void plane_land(void);

int main() {

    //generate_input_files();

    init_simlib();

    test_event_insert();

    /*
    init_simlib();
    infile = fopen("plane_list.out", "r");
    schedule_landing_list(infile);
    int num_planes = 0;

    while (list_size[LIST_EVENT] != 0) {
        timing();
        num_planes = num_planes + 1;
        printf("%d\n", num_planes);
    }

    fclose(infile);
    */
}



void test_event_insert() {
    /*event_schedule(0, 2);
    event_schedule(1, 1);
    event_schedule(2, 2);
    event_schedule(3, 3);
    event_schedule(4, 2);
    event_schedule(5, 1);
    event_schedule(6, 3);
    */event_schedule(7, 4);

    printf("%d\n", list_size[LIST_EVENT]);

    event_list_display();

    printf("\n");

    //list_delete(LIST_EVENT, 2, EVENT_TYPE);
    /*event_insert(3.5, 1);
    event_insert(3.75, 2);
    event_insert(3.9, 3);
    */event_insert(6, 1);

    event_insert(7, 2);
    event_insert(7, 5);

    printf("%d\n", list_size[LIST_EVENT]);

    event_list_display();

    //printf("\n%f %f\n", transfer[EVENT_TIME], transfer[EVENT_TYPE]);
}

/*  Generates the input files, plane_list.dat and storm_list.dat
    The format of each input file is the same:
    EVENT_TYPE <space> EVENT_TIME
    EVENT_TYPE <space> EVENT_TIME
    ....
    EVENT_TYPE <space> EVENT_TIME
    EOF
*/
void generate_input_files(void) {
    plane_list = fopen("plane_list.dat", "w");
    storm_list = fopen("storm_list.dat", "w");
    float sim_time = TIME_YEAR;

    generate_landing_list(plane_list, sim_time);
    generate_storm_list(storm_list, sim_time);

    fclose(plane_list);
    fclose(storm_list);
}


void generate_landing_list(FILE *file, float sim_time) {
    if (sim_time < TIME_LAND_FREQ-TIME_LAND_FREQ_VAR)
        return;

    float time = 0;
    while (time < sim_time) {
        float prob_distrib[] = {FREQ_PLANE1, FREQ_PLANE2, FREQ_PLANE3};
        int type = random_integer(prob_distrib, STREAM_PLANE_TYPE);
        float r = uniform(TIME_LAND_FREQ-TIME_LAND_FREQ_VAR, TIME_LAND_FREQ+TIME_LAND_FREQ_VAR, STREAM_INTERARRIVAL);
        fprintf(file, "%d %f\n", type, time + r);
        time = time + r;
    }
    return;
}

void generate_storm_list(FILE *file, float sim_time) {
    float time = 0;
    while (time < sim_time) {
        float r = expon(TIME_HOUR*48, STREAM_STORM_TIME);
        time = time + r;
        fprintf(file, "%d %f\n", EVENT_STORM_START, time);
        float duration = uniform(TIME_STORM_DUR-TIME_STORM_VAR, TIME_STORM_DUR+TIME_STORM_VAR, STREAM_STORM_DURATION);
        time = time + duration;
        fprintf(file, "%d %f\n", EVENT_STORM_END, time);
    }
    return;
}


/*  Takes an input file and uses simlib's event_schedule method
    to add all of the events in the input file to simlib's
    event queue.    */
void schedule_input_list(FILE *file) {
    int type;
    float time;
    while (fscanf(file, "%d %f", &type, &time) != EOF) {
        event_schedule(time, type);
    }
}
