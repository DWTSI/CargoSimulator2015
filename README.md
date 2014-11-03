CargoSimulator2015
==================


TODO:  Add the "delete" and "insert" functions to simlib.
---------------------------------------------------------
Delete:
	Must delete the (logically) first record from list
	"list" with a value "value" (float) for attribute
	"attribute."  Place the attributes of deleted record
	in the transfer array. 
	
	Statement of form "delete(list, value, attribute)"
	
	For error condition (e.g., there is no matching 
	record in the list), return a value of 0. Otherwise,
	return value of 1.
	
	Update statistics for list "list" by invoking timest.
	You will want to allocate memory dynamically to
	accommodate the transfer array.
	
Insert:
	Must insert a new event record into the event list, 
	using the middle-pointer algorithm discussed in 
	Section 2.8.  If the two event record have the same
	event time, give preference to the event with the 
	lowest-numbered event type.
