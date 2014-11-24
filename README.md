CargoSimulator2015
==================

TODO:
----------------------------------------------------

Add code to verify taxi behavior.
	
verify sim_completeness: 

	check that the time of the last event
	is reasonably close to the specified sim_time.
	Right now, if the simulation runs for two days, it will still
	return true for sim_completeness because it's only checking if 
	the last event occurs before the specified sim time.
	
verify_output:
	
	When verify_output() is called at the end of the simulation, 
	it hangs the program. Needs to be fixed.