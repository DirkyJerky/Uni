
var elList, addLink, counter; // Declare variables
var $ = function(id) { return document.getElementById(id); };
elList  = $('list');               // Get list                   
addLink = $('add');					// Get add item button
counter = $('counter');            // Get item counter

function addItem() {	// Declare function to add an item in the list
  var newItem = window.prompt("Please enter a new item", `New List item #${Number(counter.innerHTML) + 1}`);
  if (newItem == null) {
    return;
  }

  var newLI = document.createElement("li");
  newLI.appendChild(document.createTextNode(newItem));

  var newA = document.createElement("a");
  newA.setAttribute("href", "#");
  newA.setAttribute("class", "delete");
  newA.appendChild(document.createTextNode("Delete"));

  newLI.appendChild(newA);
  elList.appendChild(newLI);

  updateCount();
}

// create a function that remove <li> element which's delete button is clicked
function removeItem(evt){  //evt represents the event object
	if (evt.target.tagName != "A") {
    return true;
  }

  var li = evt.target.parentElement;

  if (window.confirm(`Delete ${li.childNodes[0].nodeValue} ?`)) {
    li.parentElement.removeChild(li);
    updateCount();
  }
}

function updateCount() { 		// Declare function
	var listItems;								
  listItems = elList.getElementsByTagName('li').length;  // Get total of <li>s
  counter.innerHTML = listItems;                         // Update counter
}

document.addEventListener("DOMContentLoaded", function() {
  addLink.addEventListener('click', addItem, false);
  
  elList.addEventListener('click', removeItem, false);
});


