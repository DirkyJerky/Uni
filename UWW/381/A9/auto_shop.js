// Define a car object using a constructor function
//step-1: add js code to complete the definition of the following constructor function
function Car() {
	this.stockid;
	this.make;
	this.model;
	this.year;
	this.type;
	this.color;
	this.price;
	this.mileage;
	this.display = function(){
		var this_str = "<td>" + this.stockid + "</td><td>" + this.make + "</td>";
		this_str += "<td>" + this.model + "</td><td>" + this.year + "</td><td>" + this.type + "</td>";
		this_str += "<td>" + this.color + "</td><td> $" + this.price + "</td>";
		this_str += "<td>" + this.mileage + "</td>";
		return this_str;
	}
}


// Declare an array of objects
var car_list = [];  // var car_list = new Array();

// step 2. Use a for loop to read car info from web page
// and then create the Car object instances and 
// add individual car objects to the car_list array




//step 3. apply event delegation for Add buttons, 
//by registering event handler "addItem" function to the click event to <table id='car-list'> element 
 
 

 // define an array to hold the index of the car added to the shopping chart
 var cart = [];
 
 //Step 4: add js code to complete addItem() function
 //This  function defines an event handler that adds a car to shopping cart
 function addItem(){
	//(1) Define a car index by using the value of the value attribute in each Add button element.  
	//(2) Save that car index into cart array. 
	//(3) use the car index to find the corresponding car object in the car_list array and then call addNewItemtoCart function to add selected car info (make, type, and price) into the shopping cart table. 

	
 }
 
//Step 4:(4) Add js code in addNewItemtoCart function to create three new <td> elements in shopping chart (‘mycart’) table and append them to a new table row in shop cart table to display make, 
//type, and price of the selected car.
 function addNewItemtoCart(item){
 /* This function creates and adds a new table row to an existing table
 */
	//create a new <tr> element: a table row
	var newTrElement = document.createElement('tr');
	
	//4.4.1:call createNewTdElement to create a <td> element using item.make as content
	//4.4.2: append it to the new tr element

	
	//4.4.3: call createNewTdElement to create a <td> element using item.model as content
	//4.4.4: append it to the new tr element

	
	//4.4.5: call createNewTdElement to create a <td> element using item.price as content
	//4.4.6: append it to the new tr element

	
	//append new <tr> element to the shopping cart
	document.getElementById('mycart').appendChild(newTrElement);
 }
 
 function createNewTdElement(cell_content){
 /* This function creates and returns a new table cell using  the following steps:
	1. create a new text node using createTextNode() method
	2. create a new 'td' element using createElement() method
	3. append the newly created text node to the new 'td' element
	4. return the newly created 'td' element
 */
	// create a text node
	var newTextNode = document.createTextNode(cell_content);
	// create a new td element
	var newTdElement = document.createElement('td');
	// append text node to the new td element
	newTdElement.appendChild(newTextNode);
	return newTdElement;
 }
 
 
//Step 5(1)Add js code to complete definition of the displayInvoice function
 function displayInvoice()
 {
	//use the car index in the cart array to find the corresponding car object in the car_list array, 
	//and then calculate the total number of items in the shopping cart, 
	//subtotal, 6% taxes, 5% registration fees, and total amount. 
	//Display all those invoice information in the invoice table on the web page. 
	//If the shopping cart is empty, display a pop-up message saying “Your cart is empty!” 
	
	
 }

//step 5(2)Add js code to Register displayInvoice function as the event handler to response to the click event 
//fires on the Display Invoice button.

 
 //Step 6 (1)Add js code to complete definition of the displayMinivan function
function displayMinivan()
{
	//use a for loop to access each car object in car_list array, and check each car's type property
	//if a car's type property has value "Minivan", then create a new <tr> element
	//and then add new content and table cells to new <tr> element by using innerHTML, 
	//call car object's display() method to assign a string to innerHTML.
	
	
	
}
//step 6(2) Add js code to Register displayMinivan function as the event handler 
//to response to the click event fires on the Display Minivan button. 

