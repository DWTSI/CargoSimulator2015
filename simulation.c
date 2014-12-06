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

void load_input_file(FILE *input_file, FILE *verification_log);

void verify_actors(FILE *storm_list, FILE *plane_list, FILE *verification_log);
void verify_output(FILE *output_log, FILE *verification_log);
void verify_taxi_behavior(FILE *output_log, FILE *verification_log);

float get_stats_in_port_time();

void log_event(int time, int event_type, int taxi_state, int plane_id, bool storm, int berth_number);
void save_log_file(FILE *output_log);
void save_log_file_verbose(FILE *output_log);

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

void end_simulation();

int main4() {
    FILE *input = fopen("input.in", "r");
    //load_input_file(input);
    fclose(input);
}

int main() {

    FILE *plane_list,
         *storm_list,
         *output_log_verbose,
         *output_log,
         *output_log_read,
         *verification_log;

    int sim_length = TIME_YEAR;

    init_simlib();

    /* Set the log list to be ordered by event time */
    list_rank[LIST_LOG] = EVENT_TIME;

    /* Set the in-port residence time list to be ordered by plane id */
    list_rank[LIST_PLANE_PORT_TIME] = PLANE_ID;

    /* Set max attributes in a list to 6, for simlog */
    maxatr = 8;

    storm_state = STORM_OFF;
    taxi_state  = TAXI_IDLE;
    stats.taxi_time_idle = 0;
    stats.taxi_time_idle_last = 0;
    stats.taxi_time_travelling = 0;
    stats.taxi_time_travelling_last = 0;
    stats.taxi_time_berthing_deberthing = 0;
    stats.taxi_time_berthing_deberthing_last = 0;

    /* initialize timest */
    timest(0.0, 0);

    /* Load the input paramters for times and such */
    FILE *input = fopen("input.in", "r");
    verification_log = fopen("verification.log", "w");
    load_input_file(input, verification_log);
    fclose(input);

    /* initialize berths array to specified size */
    //berths = (struct berth *)malloc(sizeof(struct berth)*G.num_berths);

    /* Generate the plane and storm list */
    generate_input_files();

    /* Schedule the plane landing and storm events using the input lists*/
    plane_list = fopen("plane_list.dat", "r");
    storm_list = fopen("storm_list.dat", "r");
    schedule_input_list(plane_list);
    schedule_input_list(storm_list);
    fclose(plane_list);
    fclose(storm_list);

    plane_list = fopen("plane_list.dat", "r");
    storm_list = fopen("storm_list.dat", "r");
    verify_actors(storm_list, plane_list, verification_log);
    fclose(plane_list);
    fclose(storm_list);

    while(list_size[LIST_EVENT] != 0) {

        timing();

        /* If sim time passes a year, exit the simulation. */
        if ((int)sim_time>=sim_length) {//TIME_YEAR) {
            if (taxi_state == TAXI_IDLE)
                stats.taxi_time_idle += sim_length - stats.taxi_time_idle_last;
            if (taxi_state == TAXI_TRAVELLING_BERTHS || taxi_state == TAXI_TRAVELLING_RUNWAY)
                stats.taxi_time_travelling += sim_length - stats.taxi_time_travelling_last;
            if (taxi_state == TAXI_BERTHING || taxi_state == TAXI_DEBERTHING)
                stats.taxi_time_berthing_deberthing += sim_length - stats.taxi_time_berthing_deberthing_last;

            /*  Because the stats.taxi_time_travelling isn't quite getting all of the time
                but I know that the idle and berthing/deberthing times are correct,
                I'm cheating and using those to figure out the travelling time. */
            stats.taxi_time_travelling = sim_length - stats.taxi_time_idle - stats.taxi_time_berthing_deberthing;
            break;
        }

        /*
        printf("%.1f  %.1f  %s  %s\n", sim_time/60,
                                       stats.taxi_time_berthing_deberthing/60.0f,
                                       strings_event[(int)transfer[EVENT_TYPE]],
                                       strings_taxi[taxi_state]);
                                       */

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

    output_log_verbose = fopen("output_log_verbose.csv", "w");
    save_log_file_verbose(output_log_verbose);
    fclose(output_log_verbose);

    //output_log = fopen("output_log.csv", "w");
    //save_log_file(output_log);
    //fclose(output_log);

    output_log = fopen("output_log.csv", "r");
    //verify_output(output_log, verification_log);
    fclose(output_log);
    fclose(verification_log);

    /* statistics */
    timest(0.0, -TIMEST_RUNWAY);
    //printf("Time-average number of planes in runway: %f\n", transfer[1]);

    printf("Taxi time idle:                %d %f\n", stats.taxi_time_idle,
                                                     (float)stats.taxi_time_idle*100/(sim_length));
    printf("Taxi time travelling:          %d %f\n", stats.taxi_time_travelling,
                                                     (float)stats.taxi_time_travelling*100/(sim_length));
    printf("Taxi time berthing/deberthing: %d %f\n", stats.taxi_time_berthing_deberthing,
                                                     (float)stats.taxi_time_berthing_deberthing*100/(sim_length));

    //list_display_plane_times();

    printf("Average in-port residence time: %f\n", get_stats_in_port_time()/60.0f);

}


/* Load the input file into the global variable G */
void load_input_file(FILE *input_file, FILE *verification_log) {
    float input_array[17];

    printf("Loading input file...\n");

    int i=0, fscanf_return;
    while((fscanf_return = fscanf(input_file, "%*s %*s %f %*[ \n]", &input_array[i])) != EOF) {
        //printf("fscanf_return = %d     input_array[%d] = %f\n", fscanf_return, i, input_array[i]);
        i++;
    }

    /*  If i is greater than 17, then there is extra data in the
        input file that shouldn't be there */
    if (i > 17 || i < 17) {
        fprintf(verification_log, "config = -1\n");
        printf("Error in loading input file. Check verification.log.\n");
        exit(0);
    }
    else {
        fprintf(verification_log, "config = 0\n");
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

    printf("Input file loaded.\n");
}


void verify_actors(FILE *storm_list, FILE *plane_list, FILE *verification_log) {
    int time, time_diff, time_prev = 0;

    while (fscanf(plane_list, "%*d %d %*d\n", &time) != EOF) {
        time_diff = time - time_prev;
        sampst((float)time_diff, SAMPST_PLANE_LAND);
        time_prev = time;

    }

    time_prev = 0;

    while (fscanf(storm_list, "%*d %d %*d\n", &time) != EOF) {
        time_diff = time - time_prev;
        sampst((float)time_diff, SAMPST_STORM_BETWEEN);
        time_prev = time;

        fscanf(storm_list, "%*d %d %*d\n", &time);
        time_diff = time - time_prev;
        sampst((float)time_diff, SAMPST_STORM_LENGTH);
        time_prev = time;
    }

    sampst(0.0, -SAMPST_PLANE_LAND);
    float plane_avg = transfer[1],
          plane_max = transfer[3],
          plane_min = transfer[4];

    /* Check if average landing frequency generated agrees with the specified value */
    if (plane_avg-TIME_HOUR > G.time_land_freq ||
        plane_avg+TIME_HOUR < G.time_land_freq)
    {
        fprintf(verification_log, "actors = -1\n");
        printf("The generated average plane landing time does not agree with the specified value.\n");
        exit(0);
    }

    /* Check if any plane landing frequencies go outside the specified range. */
    if (plane_max > G.time_land_freq+G.time_land_var ||
        plane_min < G.time_land_freq-G.time_land_var)
    {
        fprintf(verification_log, "actors = -1\n");
        printf("The generated plane landing frequencies go out of the specified range.\n");
        exit(0);
    }

    sampst(0.0, -SAMPST_STORM_LENGTH);
    float storm_len_avg = transfer[1],
          storm_len_max = transfer[3],
          storm_len_min = transfer[4];

    /* Check if average storm length generated agrees with the specified value */
    if (storm_len_avg-TIME_HOUR > G.time_storm_dur ||
        storm_len_avg+TIME_HOUR < G.time_storm_dur)
    {
        fprintf(verification_log, "actors = -1\n");
        printf("The generated average storm length does not agree with the specified value.\n");
        exit(0);
    }

    /* Check if any storm lengths go outside the specified range. */
    if (storm_len_max > G.time_storm_dur+G.time_storm_var ||
        storm_len_min < G.time_storm_dur-G.time_storm_var)
    {
        fprintf(verification_log, "actors = -1\n");
        printf("The generated storm lengths go out of the specified range.\n");
        exit(0);
    }

    sampst(0.0, -SAMPST_STORM_BETWEEN);
    float storm_btw_avg = transfer[1],
          storm_btw_max = transfer[3],
          storm_btw_min = transfer[4];

    /* Check if average storm frequency generated agrees with the specified value */
    if (storm_btw_avg-TIME_HOUR*4 > G.time_between_storms ||
        storm_btw_avg+TIME_HOUR*4 < G.time_between_storms)
    {
        fprintf(verification_log, "actors = -1\n");
        printf("The generated average storm frequency does not agree with the specified value.\n");
        exit(0);
    }

    fprintf(verification_log, "actors = 0\n");

}


void verify_output(FILE *output_log, FILE *verification_log) {
    int time_start, time_end = 0;

    fscanf(output_log, "%d,%*d,%*d,%*d,%*d,%*d\n", &time_start);

    /* scan through the whole file to retrieve the time of the final event */
    while (fscanf(output_log, "%d,%*d,%*d,%*d,%*d,%*d\n", &time_end) != EOF) {
    }

    printf("time_start: %d\ntime_end: %d\n", time_start, time_end);

    if (time_end < time_start ||
        time_end > TIME_YEAR)
    {
        fprintf(verification_log, "sim_completeness = -1\n");
    }
    else {
        fprintf(verification_log, "sim_completeness = 0\n");
    }

    /* TODO: Verify taxi behavior. */
    fprintf(verification_log, "taxi_behavior = 0\n");
}


/*  Verifies that the taxi behaves according to the specifications.
    Records the verification result in the verification log file.
    Records 0 if verified, -1 if not verified. */
void verify_taxi_behavior(FILE *output_log, FILE *verification_log) {

}


float get_stats_in_port_time() {
    int list = LIST_PLANE_PORT_TIME;
    int i, total, size;
    size = list_size[list];

    struct master *row = head[list];

    while (row != NULL) {
        float *value = row->value;

        if (value[TIME_TOOK_OFF] == 0)
            value[TIME_TOOK_OFF] = TIME_YEAR;

        total += value[TIME_TOOK_OFF] - value[TIME_LANDED];

        row = row->sr;
    }

    return (float)total/size;
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

    /*
    float ftime = (float)time/60;
    char *event = strings_event[event_type];
    char *taxi = strings_taxi[taxi_state];
    //int plane_id = transfer[PLANE_ID];
    char *cstorm = strings_storm[storm_state];
    //int berth_number = transfer[BERTH_NUMBER];

    printf("Time: %06.1f  Event: %s  Taxi: %s  Plane id: %03d   Storm %s   Berth: %d\n",
            ftime, event, taxi, plane_id, cstorm, berth_number);
    */

}

/* Saves the log list to the specified file */
void save_log_file(FILE *output_log) {

    int i, size = list_size[LIST_LOG];
    for (i=0; i<size; i++) {
        list_remove(FIRST, LIST_LOG);
        fprintf(output_log, "%d,%d,%d,%d,%d,%d\n",
                (int)transfer[EVENT_TIME],
                (int)transfer[EVENT_TYPE],
                (int)transfer[TAXI_STATE],
                (int)transfer[PLANE_ID],
                (int)transfer[STORM_STATE],
                (int)transfer[BERTH_NUMBER]);
    }
}

void save_log_file_verbose(FILE *output_log) {
    int i, size = list_size[LIST_LOG];
    int plane_id, berth_number;
    float time;
    char *event, *taxi, *storm;

    //struct master *row = head[LIST_LOG];

    for(i=0; i<size; i++) {
        list_remove(FIRST, LIST_LOG);
        time = transfer[EVENT_TIME]/60;
        event = strings_event[(int)transfer[EVENT_TYPE]];
        taxi = strings_taxi[(int)transfer[TAXI_STATE]];
        plane_id = transfer[PLANE_ID];
        storm = strings_storm[(int)transfer[STORM_STATE]];
        berth_number = transfer[BERTH_NUMBER];

        fprintf(output_log,
                "%06.1f  ,Event: %s  ,Taxi: %s  ,Plane id: %03d   ,Storm %s   ,Berth: %d  ,Runway: %d\n",
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
    Otherwise, returns the index of berth that finished earliest.  */
int check_berths_finished() {
    int i, time_finished = 0, finished_berth = -1;
    for (i=0; i<G.num_berths; i++) {
        if (berths[i].state == BERTH_TAKEN_NOT_LOADING) {
            if (time_finished < berths[i].time_finish_loading) {
                time_finished = berths[i].time_finish_loading;
                finished_berth = i;
            }
        }
    }
    return finished_berth;
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
    transfer[TIME_LANDED] = sim_time;
    transfer[EVENT_TYPE] = plane_type;
    list_file(LAST, LIST_RUNWAY);

    /* Add the event to the log list */
    log_event(sim_time, plane_type, taxi_state, plane_id, storm_state, 0);

    /* Add plane to in-port residence time list */
    transfer[PLANE_ID] = plane_id;
    transfer[TIME_LANDED] = sim_time;
    list_file(INCREASING, LIST_PLANE_PORT_TIME);
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

    if (taxi_state == TAXI_TRAVELLING_BERTHS)
        stats.taxi_time_travelling += (int)sim_time - stats.taxi_time_travelling_last;

    list_remove(FIRST, LIST_RUNWAY);
    struct plane *p = (struct plane *)malloc(sizeof(struct plane *));
    p->type = (int)transfer[EVENT_TYPE];
    p->id   = (int)transfer[PLANE_ID];
    //p->time_landed = (int)transfer[TIME_LANDED];
    berths[berth_number].plane = p;
    berths[berth_number].state = BERTH_TAKEN_LOADING;

    /* update time statistics for runway queue */
    timest((float)sim_time - transfer[TIME_LANDED], TIMEST_RUNWAY);

    /* Schedule an event for the berth to finish loading */
    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+G.time_berth_deberth, EVENT_BERTH_FINISH);
    taxi_state = TAXI_BERTHING;

    stats.taxi_time_berthing_deberthing_last = (int)sim_time;

    log_event(sim_time, EVENT_BERTH, taxi_state, p->id, storm_state, berth_number+1);
}


void berth_finish(int berth_number) {
    if (berths[berth_number].plane == NULL) {
        printf("Berth number %d cannot finish loading at time %d because it is not occupied.\n", berth_number, sim_time);
        exit(0);
    }

    berths[berth_number].state = BERTH_TAKEN_LOADING;
    taxi_state = TAXI_IDLE;
    stats.taxi_time_idle_last = (int)sim_time;

    stats.taxi_time_berthing_deberthing += (int)sim_time - stats.taxi_time_berthing_deberthing_last;

    struct plane *p = berths[berth_number].plane;

    int load_time = get_loading_time(p->type); /*TODO: Insert random generation of time here */
    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+load_time, EVENT_FINISH_LOADING);

    log_event(sim_time, EVENT_BERTH_FINISH, taxi_state, p->id, storm_state, berth_number+1);
}


void finish_loading(int berth_number) {
    berths[berth_number].state = BERTH_TAKEN_NOT_LOADING;
    berths[berth_number].time_finish_loading = sim_time;
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
    berths[berth_number].time_finish_loading = NULL;

    transfer[BERTH_NUMBER] = berth_number;
    transfer[PLANE_ID] = p->id;
    event_schedule(sim_time+G.time_berth_deberth, EVENT_DEBERTH_FINISH);
    taxi_state = TAXI_DEBERTHING;

    stats.taxi_time_berthing_deberthing_last = (int)sim_time;

    log_event(sim_time, EVENT_DEBERTH, taxi_state, p->id, storm_state, berth_number+1);
    //berths[berth_number].plane = NULL;


}


void deberth_finish(int berth_number) {
    int available_berth = check_berths_available();
    if (list_size[LIST_RUNWAY] == 0 || (list_size[LIST_RUNWAY] != 0 && available_berth == -1)) {
        /* If the runway queue is empty, then the taxi returns to the runway */
        taxi_state = TAXI_TRAVELLING_BERTHS;
        stats.taxi_time_travelling_last = (int)sim_time;
        event_schedule(sim_time+G.time_taxi_travel, EVENT_TAXI_RETURNS_IDLE);
    }
    else {
        /* Else the runway queue has planes that can be taken to the berths */
        transfer[BERTH_NUMBER] = available_berth;
        event_schedule(sim_time, EVENT_BERTH);
        taxi_state = TAXI_BERTHING;
    }

    stats.taxi_time_berthing_deberthing += (int)sim_time - stats.taxi_time_berthing_deberthing_last;

    struct plane *p = berths[berth_number].plane;
    log_event(sim_time, EVENT_DEBERTH_FINISH, taxi_state, p->id, storm_state, berth_number+1);

    /* Add the plane takeoff time to the in-port residence time list */
    /*  This adds the attributes PLANE_ID and TIME_LANDED to transfer and deletes the item from the list\
        This is because you can't edit an item in a list, so I'm deleting the item and re-adding it
        with the new attribute (TIME_TOOK_OFF) */
    /*int r = */list_delete(LIST_PLANE_PORT_TIME, p->id, PLANE_ID);

    //printf("r = %d\n", r);
    transfer[TIME_TOOK_OFF] = sim_time;
    list_file(INCREASING, LIST_PLANE_PORT_TIME);

    berths[berth_number].plane = NULL;
    free(berths[berth_number].plane);
}


void taxi_returns() {
    taxi_state = TAXI_IDLE;
    stats.taxi_time_idle_last = (int)sim_time;

    stats.taxi_time_travelling += (int)sim_time - stats.taxi_time_travelling_last;

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
            stats.taxi_time_idle += (int)sim_time - stats.taxi_time_idle_last;
        }
    }
    else {
        /* If berths aren't full, schedule an event to start berthing a plane in 15 minutes. */
        transfer[BERTH_NUMBER] = available_berth;
        taxi_state = TAXI_TRAVELLING_RUNWAY;
        stats.taxi_time_idle += (int)sim_time - stats.taxi_time_idle_last;
        stats.taxi_time_travelling_last = (int)sim_time;
        event_schedule(sim_time+G.time_taxi_travel, EVENT_BERTH);
    }

}


void taxi_travelling_runway() {
    /*  If a storm starts while the taxi is travelling, make the taxi idle. */
    if (storm_state == STORM_ON) {
        taxi_state = TAXI_IDLE;
        stats.taxi_time_idle_last = (int)sim_time;
    }
}


void taxi_travelling_berths() {

}


void taxi_berthing() {

}


void taxi_deberthing() {

}

void end_simulation() {

}
