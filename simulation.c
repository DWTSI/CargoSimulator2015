#include "simlib/simlib.h"
#include "simulation.h"


//struct plane, berth, taxi;

int storm_state, taxi_state;

//void log_event(int time, int event_type, int taxi_state, )
void plane_land();
void storm_start();
void storm_end();
void berth();
void deberth();

int main2() {

    FILE *plane_list, *storm_list;

    init_simlib();

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


void plane_land() {
    /* Add the plane in transfer to the runway queue */
    list_file(LAST, LIST_RUNWAY);
    printf("%f\n", sim_time);
}

void storm_start() {

}


void storm_end() {

}


void berth() {

}


void deberth() {

}
