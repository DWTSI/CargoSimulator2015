#ifndef SIMULATION_H
#define SIMULATION_H

/*  EVENT_LAND1, EVENT_LAND2, and EVENT_LAND3
	must stay as 1, 2, and 3, respectively
	or else schedule_input_list will
	schedule the wrong events.  */
#define EVENT_LAND1         1  /*plane 1 lands*/
#define EVENT_LAND2         2  /*plane 2 lands*/
#define EVENT_LAND3         3  /*plane 3 lands*/
#define EVENT_STORM_START   4  /*storm starts*/
#define EVENT_STORM_END     5  /*storm ends*/
#define EVENT_BERTH         6  /*a plane berths*/
#define EVENT_DEBERTH       7  /*a plane deberths and takes off*/

#define EVENT_FINISH_LOADING     8  /*plane finishes loading */
#define EVENT_BERTH_FINISH       9  /*taxi finishes berthing a plane*/
#define EVENT_DEBERTH_FINISH    10  /*plane finishes deberthing and takes off, taxi at runway*/
#define EVENT_TAXI_RETURNS_IDLE 11  /*taxi returns to the berths */


/*  transfer indices for the event log */
#define TAXI_STATE   3
#define PLANE_ID     4
#define STORM_STATE  5
#define BERTH_NUMBER 6
#define RUNWAY_SIZE  7
#define DEBERTH_QUEUE_SIZE 8
#define TIME_LANDED  9

/*  transfer indices for the in-port residence time list */
/*  TIME_LANDED and PLANE_ID will be the same as above, for convenience */
#define TIME_TOOK_OFF 1
#define PLANE_TYPE   2


/*  These define the different states for the taxi.
    These will be stored in a global taxi struct.  */
#define TAXI_IDLE              0   /* Taxi is not doing anything */
#define TAXI_TRAVELLING_RUNWAY 1   /* Taxi is traveling to runway without a plane */
#define TAXI_TRAVELLING_BERTHS 2   /* Taxi is traveling to berths without a plane */
#define TAXI_BERTHING          3   /* Taxi is berthing a plane */
#define TAXI_DEBERTHING        4   /* Taxi is deberthing a plane */



/* Time units are in minutes */
#define TIME_MINUTE    1
#define TIME_HOUR     60
#define TIME_DAY    1440
#define TIME_YEAR 525600

/*Relative frequencies of the plane types*/
#define FREQ_PLANE1 0.25
#define FREQ_PLANE2 0.25
#define FREQ_PLANE3 0.5

/* Loading times for planes. */
#define TIME_LOAD1     18*TIME_HOUR
#define TIME_LOAD1_VAR  2*TIME_HOUR
#define TIME_LOAD2     24*TIME_HOUR
#define TIME_LOAD2_VAR  4*TIME_HOUR
#define TIME_LOAD3     36*TIME_HOUR
#define TIME_LOAD3_VAR  4*TIME_HOUR

#define TIME_LAND_FREQ      TIME_HOUR*11 /*how frequently planes land*/
#define TIME_LAND_FREQ_VAR  TIME_HOUR*7  /*variation in plane landing frequency*/
#define TIME_STORM_DUR      TIME_HOUR*4  /*average duration of a storm*/
#define TIME_STORM_VAR      TIME_HOUR*2  /*variation in storm duration*/

#define TIMEST_RUNWAY 1  /* timest variable for runway queue */
#define SAMPST_PLANE_LAND    2 /* sampst variable for verifying plane landing times */
#define SAMPST_STORM_LENGTH  3
#define SAMPST_STORM_BETWEEN 4



#define STREAM_INTERARRIVAL   1
#define STREAM_PLANE_TYPE     2
#define STREAM_LOADING        3
#define STREAM_STORM_DURATION 4
#define STREAM_STORM_TIME     5

#define LIST_RUNWAY   1  /* List number for the runway queue */
#define LIST_DEBERTH  2  /* List number for the deberthing queue (can only contain planes from berth list) */
/* NOTE: May not include this, and use a custom 3-element array of structs instead */
#define LIST_BERTH    3  /* List number for the berths (max size = 3) */
#define LIST_AVG_PLANES_RUNWAY   4
#define LIST_AVG_PLANES_DEBERTH  5
#define LIST_PLANE_PORT_TIME     6
#define LIST_LOG  7

#define STORM_OFF 0
#define STORM_ON  1

#define NUMBER_OF_BERTHS 3
#define BERTH_TAKEN 1
#define BERTH_FREE  0
#define BERTH_TAKEN_LOADING     2
#define BERTH_TAKEN_NOT_LOADING 3

typedef enum { false, true } bool;


extern struct plane {
    int type;
    int id;
    float time_landed;
    float takeoff_time;
};

extern struct berth {
    struct plane *plane;
    int state;
    int time_finish_loading;

    int time_unoccupied;
    int time_unoccupied_last;
    int time_occupied;
    int time_occupied_last;
    int time_loading;
    int time_loading_last;
};

extern struct taxi {
    struct plane *plane;
    float time_idle;
    float time_travelling_no_plane;
    float time_berthing;
};

struct global {
    int time_land_freq;
    int time_land_var;
    int time_storm_dur;
    int time_storm_var;
    int time_between_storms;
    float freq_plane1;
    float freq_plane2;
    float freq_plane3;
    int time_load1;
    int time_load1_var;
    int time_load2;
    int time_load2_var;
    int time_load3;
    int time_load3_var;
    float time_taxi_travel;
    int time_berth_deberth;
    int num_berths;
    int sim_length;
}G;


/* Time statistics for the taxi states. */
struct statistics {
    int taxi_time_idle;
    int taxi_time_idle_last;
    int taxi_time_travelling;
    int taxi_time_travelling_last;
    int taxi_time_berthing_deberthing;
    int taxi_time_berthing_deberthing_last;
}stats;

extern void generate_input_files(void);
extern void schedule_input_list(FILE*);


#endif // SIMULATION_H
