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

/*  The output log shouldn't need to use this event. */
#define EVENT_FINISH_LOADING     8  /*plane finishes loading */
#define EVENT_BERTH_FINISH       9  /*taxi finishes berthing a plane*/
#define EVENT_DEBERTH_FINISH    10  /*plane finishes deberthing and takes off, taxi at runway*/
#define EVENT_TAXI_RETURNS_IDLE 11  /*taxi returns to the berths */


/*  transfer indices for the event log */
#define PLANE_TYPE   1
#define TAXI_STATE   3
#define PLANE_ID     4
#define STORM_STATE  5
#define BERTH_NUMBER 6
#define RUNWAY_SIZE  7


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
#define TIME_LAND_FREQ_VAR  TIME_HOUR*2  /*variation in plane landing frequency*/
#define TIME_STORM_DUR      TIME_HOUR*4  /*average duration of a storm*/
#define TIME_STORM_VAR      TIME_HOUR*2  /*variation in storm duration*/


/* 	This code is irrelevant to the project.
 	I was going to write a simpler simulation
 	to get familiar with simlib, and these
 	variables were going to be used for that
	one.  */
#define TIME_LANDED        TIME_HOUR*10  /*amount of time plane is on ground before taking off*/
#define TIME_LAND_VAR      TIME_HOUR*2  /*varation in the time plane is landed (plus-or-minus this value)*/
#define TIME_REPAIR        TIME_HOUR*15  /*amount of time to repair plane*/
#define TIME_REPAIR_VAR    TIME_HOUR*3  /*varation in the time taken to fix plane (plus-or-minus this value)*/

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
#define LIST_TAXI_TIMES          6
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
    float land_time;
    float takeoff_time;
};

extern struct berth {
    struct plane *plane;
    int state;
    float time_unoccupied;
    float time_occupied_not_loading;
    float time_loading;
};

extern struct taxi {
    struct plane *plane;
    float time_idle;
    float time_travelling_no_plane;
    float time_berthing;
};

extern void generate_input_files(void);
extern void schedule_input_list(FILE*);




#endif // SIMULATION_H
