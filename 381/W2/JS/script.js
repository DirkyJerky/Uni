"use strict";

function open_card() {
  document.getElementById('outside').className = 'open-card';
  init_guess();
}

function close_card() {
  document.getElementById('outside').className = '';
}


var guess;
var lower_guess, upper_guess;

function init_guess() {
  lower_guess = -1; // Due to rounding, the lowest value `guess` can be is `lower_guess + 1`
  upper_guess = 125;

  guess = Math.round(Math.random() * upper_guess);

  document.getElementById('guess-message').innerText = `I'm guessing you are ${guess} years of age.  Am I correct?`;
  document.getElementById('guess-buttons').style.visibility = 'visible';
}

function update_guess() {
  document.getElementById('guess-message').innerText = `How about ${guess} years?`;
}

function guess_older() {
  lower_guess = guess;
  
  if (lower_guess == upper_guess) {
    guess = lower_guess;
    guess_correct();
    return;
  }
  guess = Math.round((upper_guess - lower_guess) / 2) + lower_guess;

  update_guess();
}

function guess_younger() {
  upper_guess = guess;
  
  if (lower_guess == upper_guess) {
    guess = lower_guess;
    guess_correct();
    return;
  }

  guess = Math.round((upper_guess - lower_guess) / 2) + lower_guess;
  
  update_guess();
}

function guess_correct() {
  document.getElementById('guess-message').innerText = `You are ${guess} years of age!`;
  document.getElementById('guess-buttons').style.visibility = 'hidden';
}