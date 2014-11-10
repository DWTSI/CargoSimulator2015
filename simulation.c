#include "simlib/simlib.h"
#include "simulation.h"


//struct plane, berth, taxi;

int storm_state, taxi_state;

void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number);
void save_log_file(FILE *output_log);
void plane_land();
void storm_start();
void storm_end();
void berth();
void deberth();

int main() {
    init_simlib();

    list_rank[LIST_LOG] = EVENT_TIME;

    /* Test events to test out the logging system */
    log_event(2, EVENT_LAND1, TAXI_IDLE, 1, STORM_OFF, NULL);
    log_event(4, EVENT_LAND2, TAXI_IDLE, 2, STORM_OFF, NULL);
    log_event(5, EVENT_BERTH, TAXI_BERTHING, 1, STORM_OFF, 1);
    log_event(6, EVENT_BERTH, TAXI_BERTHING, 2, STORM_OFF, 2);
    log_event(8, EVENT_DEBERTH, TAXI_DEBERTHING, 1, STORM_OFF, 1);
    log_event(7, EVENT_DEBERTH, TAXI_DEBERTHING, 2, STORM_OFF, 2);


    FILE *output_log = fopen("output_log.dat", "w");
    save_log_file(output_log);
    fclose(output_log);
}

int main3() {

    FILE *plane_list, *storm_list;

    init_simlib();

    /* Set the log list to be ordered by event time */
    list_rank[LIST_LOG] = EVENT_TIME;

    /* Set max attributes in a list to 6, for simlog */
    maxatr = 6;

    storm_state = STORM_OFF;
    taxi_state  = TAXI_IDLE;

    generate_input_files();

    plane_list = fopen("plane_list.dat", "r");
    storm_list = fopen("storm_list.dat", "r");
    schedule_input_list(plane_list);
    schedule_input_list(storm_list);

    fclose(plane_list);
    fclose(storm_list);

    printf("Test");

    while(0) {//list_size[LIST_EVENT] != 0) {

        timing();

        /* Main event handler switch */
        switch(next_event_type) {
            case EVENT_LAND1:
            case EVENT_LAND2:
            case EVENT_LAND3:
                plane_land();
                break;
            case EVENT_STORM_START:
                storm_start();
                break;
            case EVENT_STORM_END:
                storm_end();
                break;
            case EVENT_BERTH:
                berth();
                break;
            case EVENT_DEBERTH:
                deberth();
                break;
        }

        /* Taxi handler */
        switch (taxi_state) {
            case TAXI_IDLE:

                break;
            case TAXI_TRAVELLING_RUNWAY:

                break;
            case TAXI_TRAVELLING_BERTHS:

                break;
            case TAXI_BERTHING:

                break;
            case TAXI_DEBERTHING:

                break;
        }
    }// end simulation loop
}

/* log some event into the log list */
/* NOT ACTUALLY TESTED YET. */
void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number) {
    transfer[1] = time;
    transfer[2] = event_type;
    transfer[3] = taxi_state;
    transfer[4] = plane_id;
    transfer[5] = storm;
    transfer[6] = berth_number;

    /* Add attributes in transfer to log list */
    if (list_size[LIST_LOG] == 0) {
        list_file(FIRST, LIST_LOG);
    }
    else {
        list_file(INCREASING, LIST_LOG);
    }
}

/* Saves the log list to the specified file */
void save_log_file(FILE *output_log) {

    int i, size = list_size[LIST_LOG];
    for (i=0; i<size; i++) {
        list_remove(FIRST, LIST_LOG);
        fprintf(output_log, "%d,%d,%d,%d,%d,%d\n",
                (int)transfer[1], (int)transfer[2], (int)transfer[3],
                (int)transfer[4], (int)transfer[5], (int)transfer[6]);
    }
}

void plane_land() {
    /* Add the plane in transfer to the runway queue */
    list_file(LAST, LIST_RUNWAY);
    //printf("%f\n", sim_time);
}

void storm_start() {

}


void storm_end() {

}


void berth() {

}


void deberth() {

}
