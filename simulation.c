#include "simlib/simlib.h"
#include "simulation.h"


//struct plane, berth, taxi;

int storm_state, taxi_state;
struct berth berths[NUMBER_OF_BERTHS];

void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number);
void save_log_file(FILE *output_log);

int check_berths();

void plane_land(int plane_type, int plane_id);
void storm_start();
void storm_end();
void berth(int berth_number);
void deberth(int berth_number);
void berth_finish(int berth_number);

void taxi_idle();
void taxi_travelling_runway();
void taxi_travelling_berths();
void taxi_berthing();
void taxi_deberthing();

int main() {

    FILE *plane_list, *storm_list, *output_log;

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

    while(list_size[LIST_EVENT] != 0) {

        timing();

        //printf("Next event time: %.1f,  Next event type: %d\n", transfer[EVENT_TIME]/60, (int)transfer[EVENT_TYPE]);

        /* Main event handler switch */
        switch(next_event_type) {

            case EVENT_LAND1:
            case EVENT_LAND2:
            case EVENT_LAND3:
                plane_land(next_event_type, transfer[EVENT_PLANE_ID]);
                break;
            case EVENT_STORM_START:
                storm_start();
                break;
            case EVENT_STORM_END:
                storm_end();
                break;
            case EVENT_BERTH:
                berth(transfer[BERTH_NUMBER]);
                break;
            case EVENT_DEBERTH:
                deberth(transfer[BERTH_NUMBER]);
                break;
            case EVENT_BERTH_FINISH:
                berth_finish(transfer[BERTH_NUMBER]);
                break;
        }

        /* Taxi handler */
        switch (taxi_state) {
            case TAXI_IDLE:
                taxi_idle();
                break;
            case TAXI_TRAVELLING_RUNWAY:
                taxi_travelling_runway();
                break;
            case TAXI_TRAVELLING_BERTHS:
                taxi_travelling_berths();
                break;
            case TAXI_BERTHING:
                taxi_berthing();
                break;
            case TAXI_DEBERTHING:
                taxi_deberthing();
                break;
        }
    }// end simulation loop

    printf("Log list size: %d\n", list_size[LIST_LOG]);

    output_log = fopen("output_log.dat", "w");
    save_log_file(output_log);
    fclose(output_log);
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
        fprintf(output_log, "%.1f,%d,%d,%d,%d,%d\n",
                transfer[1]/60, (int)transfer[2], (int)transfer[3],
                (int)transfer[4], (int)transfer[5], (int)transfer[6]);
    }
}



/*  Checks if there are any available berths.
    Returns a -1 if all the berths are full.
    Returns the index of the first available berth otherwise.  */
int check_berths_available() {
    int i;
    for (i=0; i<NUMBER_OF_BERTHS; i++) {
        if (berths[i].plane == NULL)
            return i;
    }
    return -1;
}

/*  Checks to see if any berths have a plane that is finished loading.
    Returns a -1 if no berths have a plane that is finished loading.
    Otherwise, returns the index of the first finished berth.  */
int check_berths_finished() {
    int i;
    for (i=0; i<NUMBER_OF_BERTHS; i++) {
        if (berths[i].state == BERTH_TAKEN_NOT_LOADING)
            return i;
    }
    return -1;
}


void plane_land(int plane_type, int plane_id) {
    /* Add the plane in transfer to the runway queue */
    transfer[PLANE_ID] = plane_id;
    list_file(LAST, LIST_RUNWAY);

    /* Add the event to the log list */
    log_event(sim_time, plane_type, taxi_state, plane_id, storm_state, 0);
}

void storm_start() {
    /* Set the global variable storm_state */
    storm_state = STORM_ON;

    /* Add the event to the log list */
    log_event(sim_time, EVENT_STORM_START, taxi_state, 0, STORM_ON, 0);
}


void storm_end() {
    /* Set the global variable storm_state */
    storm_state = STORM_OFF;

    /* Add the event to the log list */
    log_event(sim_time, EVENT_STORM_END, taxi_state, 0, STORM_OFF, 0);
}


void berth(int berth_number) {
    if (berths[berth_number].plane != NULL) {
        printf("Berth number %d cannot berth a plane at time %d because it is occupied.\n", berth_number, sim_time);
        exit(0);
    }

    list_remove(FIRST, LIST_RUNWAY);
    struct plane *p = (struct plane *)malloc(sizeof(struct plane *));
    p->type = (int)transfer[1];
    p->id   = (int)transfer[PLANE_ID];
    berths[berth_number].plane = p;
    berths[berth_number].state = BERTH_TAKEN_LOADING;

    /* Schedule an event for the berth to finish loading */
    transfer[BERTH_NUMBER] = berth_number;
    event_schedule(sim_time+TIME_HOUR, EVENT_BERTH_FINISH);
    taxi_state = TAXI_BERTHING;

    log_event(sim_time, EVENT_BERTH, taxi_state, transfer[PLANE_ID], storm_state, berth_number);
}


void deberth(int berth_number) {
    if (berths[berth_number].plane == NULL) {
        printf("Berth number %d cannot deberth a plane at time %d because it is not occupied.\n", berth_number, sim_time);
        exit(0);
    }

    if (berths[berth_number].state == BERTH_TAKEN_LOADING) {
        printf("Berth number %d cannot deberth a plane at time %d because it is not finished unloading.\n", berth_number, sim_time);
        exit(0);
    }


}


void berth_finish(int berth_number) {
    if (berths[berth_number].plane == NULL) {
        printf("Berth number %d cannot finish loading at time %d because it is not occupied.\n", berth_number, sim_time);
        exit(0);
    }

    berths[berth_number].state = BERTH_TAKEN_NOT_LOADING;
    taxi_state = TAXI_IDLE;
}


void taxi_idle() {
    /* Do nothing if storm is occurring */
    if (storm_state == STORM_ON)
        return;

    /* Do nothing if runway queue is empty */
    if (list_size[LIST_RUNWAY] == 0)
        return;

    int finished_berth = check_berths_finished();

    int available_berth = check_berths_available();

    printf("Available berth: %d\n", available_berth);

    /* If the berths are full, check if any of them are finished. */
    if (available_berth == -1)
        return;

    //printf("Next event time: %.1f,  Next event type: %d\n", transfer[EVENT_TIME]/60, (int)transfer[EVENT_TYPE]);

    /* Otherwise, schedule an event to start berthing a plane in 15 minutes. */
    transfer[BERTH_NUMBER] = available_berth;
    event_schedule(sim_time+15, EVENT_BERTH);
    taxi_state = TAXI_TRAVELLING_RUNWAY;

}


void taxi_travelling_runway() {
    /*  If a storm starts while the taxi is travelling, make the taxi idle. */
    if (storm_state = STORM_ON)
        taxi_state = TAXI_IDLE;
}


void taxi_travelling_berths() {

}


void taxi_berthing() {

}


void taxi_deberthing() {

}
