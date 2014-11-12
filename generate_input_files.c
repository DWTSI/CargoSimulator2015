#include "simlib/simlib.h"
#include "simulation.h"
#include <math.h>

FILE *plane_list, *storm_list;

void test_event_insert();
void generate_input_files(void);
void generate_landing_list(FILE*, int);
void generate_storm_list(FILE*, int);
void schedule_input_list(FILE*);
void plane_land(void);

int main2() {

    printf("%d\n", TIME_DAY);
    printf("%f\n", (float)TIME_DAY);

    generate_input_files();

    //init_simlib();

    //test_event_insert();

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


/* testing out the event_insert method */
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
    EVENT_TYPE <space> EVENT_TIME <space> PLANE_ID
    EVENT_TYPE <space> EVENT_TIME <space> PLANE_ID
    ....
    EVENT_TYPE <space> EVENT_TIME <space> PLANE_ID
    EOF
*/
void generate_input_files(void) {
    plane_list = fopen("plane_list.dat", "w");
    storm_list = fopen("storm_list.dat", "w");
    int sim_time = TIME_YEAR;

    generate_landing_list(plane_list, sim_time);
    generate_storm_list(storm_list, sim_time);

    fclose(plane_list);
    fclose(storm_list);
}


void generate_landing_list(FILE *file, int sim_time) {
    if (sim_time < TIME_LAND_FREQ-TIME_LAND_FREQ_VAR)
        return;

    int time = 0, plane_id=1;
    while (time < sim_time) {
        float prob_distrib[] = {FREQ_PLANE1, FREQ_PLANE2, FREQ_PLANE3};
        int type = random_integer(prob_distrib, STREAM_PLANE_TYPE);
        int r = (int)uniform(TIME_LAND_FREQ-TIME_LAND_FREQ_VAR, TIME_LAND_FREQ+TIME_LAND_FREQ_VAR, STREAM_INTERARRIVAL);
        fprintf(file, "%d %d %d\n", type, time + r, plane_id);
        time = time + r;
        plane_id++;
    }
    return;
}

void generate_storm_list(FILE *file, int sim_time) {
    int time = 0;

    FILE *output_csv = fopen("storm_list.csv", "w");

    while (time < sim_time) {
        int r = (int)expon(TIME_DAY*2, STREAM_STORM_TIME);
        time = time + r;
        fprintf(file, "%d %d %d\n", EVENT_STORM_START, time, 0);
        fprintf(output_csv, "Time: %06.1f,,%d\n", (float)time/60, EVENT_STORM_START);
        int duration = (int)uniform(TIME_STORM_DUR-TIME_STORM_VAR, TIME_STORM_DUR+TIME_STORM_VAR, STREAM_STORM_DURATION);
        time = time + duration;
        fprintf(file, "%d %d %d\n", EVENT_STORM_END, time,0);
        fprintf(output_csv, "Time: %06.1f, Duration: %4.1f, %d\n", (float)time/60, (float)duration/60, EVENT_STORM_END);
    }
    fclose(output_csv);
    return;
}


/*  Takes an input file and uses simlib's event_schedule method
    to add all of the events in the input file to simlib's
    event queue.    */
void schedule_input_list(FILE *file) {
    int event_type, event_time, plane_id;
    while (fscanf(file, "%d %d %d", &event_type, &event_time, &plane_id) != EOF) {
        transfer[EVENT_PLANE_ID] = plane_id;
        event_schedule(event_time, event_type);
    }
}
