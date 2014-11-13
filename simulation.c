#include "simlib/simlib.h"
#include "simulation.h"

char *strings_event[] = {"Null            ",
                         "Land plane 1    ",
                         "Land plane 2    ",
                         "Land plane 3    ",
                         "Storm starts    ",
                         "Storm ends      ",
                         "Berth plane     ",
                         "Deberth plane   ",
                         "Loading finished",
                         "Berth finishes  ",
                         "Deberth finishes",
                         "Taxi returns    "};

char *strings_storm[] = {"Off",
                         "On "};

char *strings_taxi[] = {"Idle                ",
                        "Travelling to runway",
                        "Travelling to berths",
                        "Berthing a plane    ",
                        "Deberthing a plane  "};


//struct plane, berth, taxi;

int storm_state, taxi_state;
struct berth berths[NUMBER_OF_BERTHS];

void load_input_file(FILE *input_file);

void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number);
void save_log_file(FILE *output_log);

int check_berths_available();
int check_berths_finished();
int get_loading_time(int plane_type);

void plane_land(int plane_type, int plane_id);
void storm_start();
void storm_end();
void berth(int berth_number);
void deberth(int berth_number);
void finish_loading(int berth_number);
void berth_finish(int berth_number);
void deberth_finish(int berth_number);
void taxi_returns();

void taxi_idle();
void taxi_travelling_runway();
void taxi_travelling_berths();
void taxi_berthing();
void taxi_deberthing();

int main4() {
    FILE *input = fopen("input.in", "r");
    load_input_file(input);
    fclose(input);
}

int main() {

    FILE *plane_list, *storm_list, *output_log;

    init_simlib();

    /* Set the log list to be ordered by event time */
    list_rank[LIST_LOG] = EVENT_TIME;

    /* Set max attributes in a list to 6, for simlog */
    maxatr = 7;

    storm_state = STORM_OFF;
    taxi_state  = TAXI_IDLE;

    /* Load the input paramters for times and such */
    FILE *input = fopen("input.in", "r");
    load_input_file(input);
    fclose(input);

    /* Generate the plane and storm list */
    generate_input_files();

    /* Schedule the plane landing and storm events using the input lists*/
    plane_list = fopen("plane_list.dat", "r");
    storm_list = fopen("storm_list.dat", "r");
    schedule_input_list(plane_list);
    schedule_input_list(storm_list);
    fclose(plane_list);
    fclose(storm_list);

    while(list_size[LIST_EVENT] != 0  &&  sim_time<TIME_YEAR) {

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
            case EVENT_FINISH_LOADING:
                finish_loading(transfer[BERTH_NUMBER]);
                break;
            case EVENT_BERTH_FINISH:
                berth_finish(transfer[BERTH_NUMBER]);
                break;
            case EVENT_DEBERTH_FINISH:
                deberth_finish(transfer[BERTH_NUMBER]);
                break;
            case EVENT_TAXI_RETURNS_IDLE:
                taxi_returns();
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

    output_log = fopen("output_log.csv", "w");
    save_log_file_verbose(output_log);
    fclose(output_log);
}


/* Load the input file into the global variable G */
void load_input_file(FILE *input_file) {
    float input_array[17];

    int i=0;
    while(fscanf(input_file, "%*s %*s %f %*[ \n]", &input_array[i]) != EOF) {
        i++;
    }

    G.time_land_freq        = input_array[0]*TIME_HOUR;
    G.time_land_var         = input_array[1]*TIME_HOUR;
    G.time_storm_dur        = input_array[2]*TIME_HOUR;
    G.time_storm_var        = input_array[3]*TIME_HOUR;
    G.time_between_storms   = input_array[4]*TIME_HOUR;
    G.freq_plane1           = (float)input_array[5];
    G.freq_plane2           = (float)input_array[6];
    G.freq_plane3           = (float)input_array[7];
    G.time_load1            = input_array[8]*TIME_HOUR;
    G.time_load1_var        = input_array[9]*TIME_HOUR;
    G.time_load2            = input_array[10]*TIME_HOUR;
    G.time_load2_var        = input_array[11]*TIME_HOUR;
    G.time_load3            = input_array[12]*TIME_HOUR;
    G.time_load3_var        = input_array[13]*TIME_HOUR;
    G.time_taxi_travel      = (float)input_array[14]*TIME_HOUR;
    G.time_berth_deberth    = input_array[15]*TIME_HOUR;
    G.num_berths            = input_array[16];
}


/* log some event into the log list */
void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number) {
    transfer[1] = time;
    transfer[2] = event_type;
    transfer[3] = taxi_state;
    transfer[4] = plane_id;
    transfer[5] = storm;
    transfer[6] = berth_number;
    transfer[7] = list_size[LIST_RUNWAY];

    /* Add attributes in transfer to log list */
    if (list_size[LIST_LOG] == 0) {
        list_file(FIRST, LIST_LOG);
    }
    else {
        list_file(INCREASING, LIST_LOG);
    }


    float ftime = (float)time/60;
    char *event = strings_event[event_type];
    char *taxi = strings_taxi[taxi_state];
    //int plane_id = transfer[PLANE_ID];
    char *cstorm = strings_storm[storm_state];
    //int berth_number = transfer[BERTH_NUMBER];

    printf("Time: %06.1f  Event: %s  Taxi: %s  Plane id: %03d   Storm %s   Berth: %d\n",
            ftime, event, taxi, plane_id, cstorm, berth_number);


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

void save_log_file_verbose(FILE *output_log) {
    int i, size = list_size[LIST_LOG];
    int plane_id, berth_number;
    float time;
    char *event, *taxi, *storm;
    for(i=0; i<size; i++) {
        list_remove(FIRST, LIST_LOG);
        time = transfer[EVENT_TIME]/60;
        event = strings_event[(int)transfer[EVENT_TYPE]];
        taxi = strings_taxi[(int)transfer[TAXI_STATE]];
        plane_id = transfer[PLANE_ID];
        storm = strings_storm[(int)transfer[STORM_STATE]];
        berth_number = transfer[BERTH_NUMBER];

        fprintf(output_log,
                "Time: %06.1f  ,Event: %s  ,Taxi: %s  ,Plane id: %03d   ,Storm %s   ,Berth: %d  ,Runway: %d\n",
                time, event, taxi, plane_id, storm, berth_number, (int)transfer[RUNWAY_SIZE]);
    }
}



/*  Checks if there are any available berths.
    Returns a -1 if all the berths are full.
    Returns the index of the first available berth otherwise.  */
int check_berths_available() {
    int i;
    for (i=0; i<G.num_berths; i++) {
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
    for (i=0; i<G.num_berths; i++) {
        if (berths[i].state == BERTH_TAKEN_NOT_LOADING)
            return i;
    }
    return -1;
}


int get_loading_time(int plane_type) {
    int time = 0, var = 0;

    if (plane_type == 1) {
        time = G.time_load1;
        var = G.time_load1_var;
    }
    else if (plane_type == 2) {
        time = G.time_load2;
        var = G.time_load2_var;
    }
    else if (plane_type == 3) {
        time = G.time_load3;
        var = G.time_load3_var;
    }

    int load_time = (int)uniform(time-var, time+var, STREAM_LOADING);

    return load_time;
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
        printf("Berth number %d cannot berth a plane at time %f because it is occupied.\n", berth_number+1, sim_time/60);
        exit(0);
    }

    list_remove(FIRST, LIST_RUNWAY);
    struct plane *p = (struct plane *)malloc(sizeof(struct plane *));
    p->type = (int)transfer[EVENT_TYPE];
    p->id   = (int)transfer[PLANE_ID];
    berths[berth_number].plane = p;
    berths[berth_number].state = BERTH_TAKEN_LOADING;

    /* Schedule an event for the berth to finish loading */
    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+G.time_berth_deberth, EVENT_BERTH_FINISH);
    taxi_state = TAXI_BERTHING;

    log_event(sim_time, EVENT_BERTH, taxi_state, p->id, storm_state, berth_number+1);
}


void berth_finish(int berth_number) {
    if (berths[berth_number].plane == NULL) {
        printf("Berth number %d cannot finish loading at time %d because it is not occupied.\n", berth_number, sim_time);
        exit(0);
    }

    berths[berth_number].state = BERTH_TAKEN_LOADING;
    taxi_state = TAXI_IDLE;

    struct plane *p = berths[berth_number].plane;

    int load_time = get_loading_time(p->type); /*TODO: Insert random generation of time here */
    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+load_time, EVENT_FINISH_LOADING);

    log_event(sim_time, EVENT_BERTH_FINISH, taxi_state, p->id, storm_state, berth_number+1);
}


void finish_loading(int berth_number) {
    berths[berth_number].state = BERTH_TAKEN_NOT_LOADING;
    struct plane *p = berths[berth_number].plane;

    log_event(sim_time, EVENT_FINISH_LOADING, taxi_state, p->id, storm_state, berth_number+1);
}


void deberth(int berth_number) {
    if (berths[berth_number].plane == NULL) {
        printf("Berth number %d cannot deberth a plane at time %d because it is not occupied.\n", berth_number+1, sim_time);
        exit(0);
    }

    if (berths[berth_number].state == BERTH_TAKEN_LOADING) {
        printf("Berth number %d cannot deberth a plane at time %d because it is not finished unloading.\n", berth_number, sim_time);
        exit(0);
    }

    /* Remove the plane from the berth */
    struct plane *p = berths[berth_number].plane;


    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+G.time_berth_deberth, EVENT_DEBERTH_FINISH);
    taxi_state = TAXI_DEBERTHING;

    log_event(sim_time, EVENT_DEBERTH, taxi_state, p->id, storm_state, berth_number+1);
    //berths[berth_number].plane = NULL;


}


void deberth_finish(int berth_number) {
    int available_berth = check_berths_available();
    if (list_size[LIST_RUNWAY] == 0 || (list_size[LIST_RUNWAY] != 0 && available_berth == -1)) {
        /* If the runway queue is empty, then the taxi returns to the runway */
        taxi_state = TAXI_TRAVELLING_BERTHS;
        event_schedule(sim_time+TIME_HOUR*G.time_taxi_travel, EVENT_TAXI_RETURNS_IDLE);
    }
    else {
        /* Else the runway queue has planes that can be taken to the berths */
        transfer[BERTH_NUMBER] = available_berth;
        event_schedule(sim_time, EVENT_BERTH);
        taxi_state = TAXI_BERTHING;
    }

    struct plane *p = berths[berth_number].plane;
    log_event(sim_time, EVENT_DEBERTH_FINISH, taxi_state, p->id, storm_state, berth_number+1);
    berths[berth_number].plane = NULL;
    free(berths[berth_number].plane);
}


void taxi_returns() {
    taxi_state = TAXI_IDLE;

    log_event(sim_time, EVENT_TAXI_RETURNS_IDLE, taxi_state, 0, storm_state, 0);
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

    /* Check if berths are full */
    if (available_berth == -1) {
        /* If berths are full and none are finished, do nothing */
        if (finished_berth == -1) {
            return;
        }
        /* If berths are full and one is finished, deberth that plane */
        else {

            transfer[BERTH_NUMBER] = finished_berth;
            event_schedule(sim_time, EVENT_DEBERTH);
            taxi_state = TAXI_DEBERTHING;
            //deberth(finished_berth);
        }
    }
    else {
        /* If berths aren't full, schedule an event to start berthing a plane in 15 minutes. */
        transfer[BERTH_NUMBER] = available_berth;
        event_schedule(sim_time+TIME_HOUR*G.time_taxi_travel, EVENT_BERTH);
        taxi_state = TAXI_TRAVELLING_RUNWAY;
    }

}


void taxi_travelling_runway() {
    /*  If a storm starts while the taxi is travelling, make the taxi idle. */
    if (storm_state == STORM_ON)
        taxi_state = TAXI_IDLE;
}


void taxi_travelling_berths() {

}


void taxi_berthing() {

}


void taxi_deberthing() {

}
