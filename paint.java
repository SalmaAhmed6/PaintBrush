import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class paint extends java.applet.Applet {
    private int x1, x2, y1, y2, state;
    private boolean fill;
    private Color color;
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Shape> spare = new ArrayList<>();
    private boolean flag;
    private boolean flagc = false;

    private Button bRectangle, bLine, bRed, bGreen, bBlue, bFill, bCircle, bEraser, bPencil, bClear, bUndo;

    abstract static class Shape {
        Color color;
        boolean solid;
        boolean flag;
        int state;
        protected int x1, x2, y1, y2;

        public Shape() {
        }

        public Shape(int x1, int y1, int x2, int y2, int state, boolean solid, Color color) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.state = state;
            this.color = color;
            this.solid = solid;
        }

        abstract public void draw(Graphics g);

        public int getx1() {
            return x1;
        }

        public int getx2() {
            return x2;
        }

        public int gety1() {
            return y1;
        }

        public int gety2() {
            return y2;
        }
    }

    class Oval extends Shape {
        Oval(int x1, int y1, int x2, int y2, int state, boolean solid, Color color) {
            super(x1, y1, x2, y2, state, solid, color);
        }

        public void draw(Graphics g) {
            if (solid)
                g.fillOval(x1, y1, x2, y2);
            else g.drawOval(x1, y1, x2, y2);
        }
    }

    class Rectangle extends Shape {
        Rectangle(int x1, int y1, int x2, int y2, int state, boolean solid, Color color) {
            super(x1, y1, x2, y2, state, solid, color);
        }

        public void draw(Graphics g) {
            if (solid)
                g.fillRect(x1, y1, x2, y2);
            else g.drawRect(x1, y1, x2, y2);
        }
    }

    class Line extends Shape {
        Line(int x1, int y1, int x2, int y2, int state, boolean solid, Color color) {
            super(x1, y1, x2, y2, state, solid, color);
        }

        public void draw(Graphics g) {
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public void init() {
        x1 = 0;
        x2 = 0;
        y1 = 0;
        y2 = 0;
        state = 0;
        fill = false;
        color = Color.black;
        flag = false;

        for (int i = 0; i < shapes.size(); i++)
            shapes.add(new Rectangle(0, 0, 0, 0, 0, fill, color));

        addMouseListener(new Click());
        addMouseMotionListener(new Click());

        initializeButtons();
    }

    private void initializeButtons() {
        bRectangle = createButton("Rectangle");
        bLine = createButton("Line");
        bCircle = createButton("Circle");
        bPencil = createButton("Pencil");
        bEraser = createButton("Erase");
        bFill = createButton("Fill");
        bClear = createButton("ClearAll");
        bUndo = createButton("Undo");
        bRed = createButton("Red");
        bGreen = createButton("Green");
        bBlue = createButton("Blue");
    }

    private Button createButton(String label) {
        Button button = new Button(label);
        button.addActionListener(new DrawAction());
        add(button);
        return button;
    }

    public void paint(Graphics g) {
        if (flagc == false) {
            for (Shape shape : shapes) {
                g.setColor(shape.color);
                shape.draw(g);
            }

            int minX = Math.min(x1, x2);
            int minY = Math.min(y1, y2);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);

            g.setColor(color);
            if (state == 1)
                if (fill)
                    g.fillRect(minX, minY, width, height);
                else
                    g.drawRect(minX, minY, width, height);
            else if (state == 2)
                g.drawLine(x1, y1, x2, y2);
            else if (state == 3) {
                if (fill)
                    g.fillOval(minX, minY, width, height);
                else g.drawOval(minX, minY, width, height);
            }
        } else {
            flagc = false;
        }
    }

    class DrawAction implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (ev.getSource() == bRed)
                color = Color.red;
            else if (ev.getSource() == bGreen) {
                color = Color.green;
            } else if (ev.getSource() == bBlue)
                color = Color.blue;
            else if (ev.getSource() == bRectangle)
                state = 1;
            else if (ev.getSource() == bLine)
                state = 2;
            else if (ev.getSource() == bCircle)
                state = 3;
            else if (ev.getSource() == bEraser) {
                state = 4;
            } else if (ev.getSource() == bPencil) {
                state = 5;
            } else if (ev.getSource() == bClear) {
                spare = new ArrayList<>(shapes);
                flag = true;
                shapes.clear();
                flagc = true;
                repaint();
            } else if (ev.getSource() == bFill) {
                fill = !fill;
                bFill.setLabel(fill ? "Unfill" : "Fill");
            } else if (ev.getSource() == bUndo) {
                if (flag) {
                    flag = false;
                    shapes.addAll(spare);
                    repaint();
                } else if (shapes.size() != 0) {
                    shapes.remove(shapes.size() - 1);
                    repaint();
                }
            }
        }
    }

    class Click implements MouseListener, MouseMotionListener {
        public void mousePressed(MouseEvent e) {
            x1 = e.getX();
            y1 = e.getY();
        }

        public void mouseDragged(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            if (state == 4) {
                shapes.add(new Rectangle(x1, y1, 10, 10, state, true, getBackground()));
                x1 = x2;
                y1 = y2;
            } else if (state == 5) {
                shapes.add(new Rectangle(x1, y1, 5, 5, state, true, color));
                x1 = x2;
                y1 = y2;
            } else
                repaint();
        }

        public void mouseReleased(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            Shape newShape = null;
            int minX = Math.min(x1, x2);
            int minY = Math.min(y1, y2);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);

            switch (state) {
                case 1:
                    newShape = new Rectangle(minX, minY, width, height, state, fill, color);
                    break;
                case 2:
                    newShape = new Line(x1, y1, x2, y2, state, fill, color);
                    break;
                case 3:
                    newShape = new Oval(minX, minY, width, height, state, fill, color);
                    break;
                case 4:
                    newShape = new Rectangle(x1, y1, 10, 10, state, true, getBackground());
                    break;
                case 5:
                    newShape = new Rectangle(x1, y1, 5, 5, state, true, color);
                    break;
            }

            if (newShape != null) {
                shapes.add(newShape);
                repaint();
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }
    }
}
