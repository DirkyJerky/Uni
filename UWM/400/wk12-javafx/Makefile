run: DessertGame.class
	java --module-path JavaFXForLinux/ --add-modules javafx.controls DessertGame

DessertGame.class: DessertGame.java
	javac --module-path JavaFXForLinux/ --add-modules javafx.controls DessertGame.java

clean:
	rm DessertGame*.class

preview:
	eog game_screenshot.png
