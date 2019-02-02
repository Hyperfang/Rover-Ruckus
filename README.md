# 11503 Hyperfang
**11503 Hyperfang** is a high-school robotics team that competes under the *FIRST* Tech Challenge. This repository contains our code for the 2018-2019 season, Rover Ruckus.

This year, the software team is composed of 3 members: *Software Lead (and APM) Caleb Browne*, *Daniel Kasabov*, and *Shaan Kumar*.

For more information about **11503 Hyperfang**, visit [our website](https://www.11503hyperfang.org/).

For more information about the *FIRST* organization, visit the [FIRST website](https://www.firstinspires.org/).

## Concepts & Features
**11503 Hyperfang** uses multiple concepts and features in our code to create a successful application for the robot. Some of the features of our project include:

* **Object Oriented Programming** - The main part of our project is programmed using a programming paradigm known as Object Oriented Programming. This allows us to represent the modularity of our robot through code.

* **Singleton Design Pattern** - Ensuring that the robot initializes one object per sub-component is vital in decreasing initializing speeds and
making sure extra resources aren't wasted.

* **State Machine** - Our autonomous uses a Finite-state machine to run the robot. A state machine allows us to react to the game based on states for a consistent, one-class autonomous.

* **PID Controller** - Precise control of our robot movement is important, so we achieve this by adding a Proportional-Integral-Derivative controller to our movement methods.

* **Computer Vision** - One way we can sense objects (Gold Cube, Silver Ball), is by utilizing an Open Source Computer Vision Library called OpenCV. Using OpenCV allows us to interact with the game field in numerous ways.

* **Augmented Reality** - In addition to Computer Vision, we also use Vuforia: an Augmented Reality SDK that allows us to recognize objects (vuMarks), and position the robot based on the sensed object.

* **Machine Learning** - Another way we can detect objects (Gold Cube, Silver Ball) is through TensorFlow (lite), a machine learning library which allows us to use Neural Networks.

### Javadoc Reference Material
This project uses an amount of code from the FTC SDK. Javadoc reference documentation for the FTC SDK is accessible [online](http://ftctechnh.github.io/ftc_app/doc/javadoc/index).
