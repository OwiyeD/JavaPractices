/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;


public abstract class Shape {

    public void printMe() {
        System.out.println("I am a shape");
    }

    public abstract double computeArea();
}

class Circle extends Shape {

    private final double rad = 5;

    @Override
    public void printMe() {
        System.out.println("I am a Circle");

    }

    public double computeArea() {
        return rad * rad * 3.142;
    }
}

class Ambigous extends Shape {

    private final double area = 10;

    public double computeArea() {
        return area;
    }
}

class display {

    public static void main(String[] args) {
        Shape[] shapes = new Shape[2];
        Circle circle = new Circle();
        Ambigous ambigous = new Ambigous();
        shapes[0] = circle;
        shapes[1] = ambigous;
        for (Shape s : shapes) {
            s.printMe();
            System.out.println(s.computeArea());
        }

    }
}
