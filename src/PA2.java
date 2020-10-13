/**
 * PA2.java - driver for the hand model simulation
 * 
 * History:
 * 
 * 19 February 2011
 * 
 * - added documentation
 * 
 * (Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>)
 * 
 * 16 January 2008
 * 
 * - translated from C code by Stan Sclaroff
 * 
 * (Tai-Peng Tian <tiantp@gmail.com>)
 * 
 */


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * The main class which drives the spider model simulation.
 * 
 * @author Tian Chen
 * @author Tian Chen
 * @since Fall 2020
 */
public class PA2 extends JFrame implements GLEventListener, KeyListener,
    MouseListener, MouseMotionListener {

  /**
   * A limb which has a limb joint, a middle joint, and a distal joint.
   * 
   * @author Tian Chen
   * @since Fall 2020
   */
  private class Leg {
    /** The distal joint of this limb. */
    private final Component distalJoint;
    /** The list of all the joints in this limb. */
    private final List<Component> joints;
    /** The middle joint of this limb. */
    private final Component middleJoint;
    /** The palm joint of this limb. */
    private final Component limbJoint;

    /**
     * Instantiates this limb with the three specified joints.
     * 
     * @param limbJoint
     *          The palm joint of this limb.
     * @param middleJoint
     *          The middle joint of this limb.
     * @param distalJoint
     *          The distal joint of this limb.
     */
    public Leg(final Component limbJoint, final Component middleJoint,
               final Component distalJoint) {
      this.limbJoint = limbJoint;
      this.middleJoint = middleJoint;
      this.distalJoint = distalJoint;

      this.joints = Collections.unmodifiableList(Arrays.asList(this.limbJoint,
          this.middleJoint, this.distalJoint));
    }

    /**
     * Gets the distal joint of this limb.
     * 
     * @return The distal joint of this limb.
     */
    Component distalJoint() {
      return this.distalJoint;
    }

    /**
     * Gets an unmodifiable view of the list of the joints of this limb.
     * 
     * @return An unmodifiable view of the list of the joints of this limb.
     */
    List<Component> joints() {
      return this.joints;
    }

    /**
     * Gets the middle joint of this limb.
     * 
     * @return The middle joint of this limb.
     */
    Component middleJoint() {
      return this.middleJoint;
    }

    /**
     * Gets the palm joint of this limb.
     * 
     * @return The palm joint of this limb.
     */
    Component palmJoint() {
      return this.limbJoint;
    }
  }

  /** The color for components which are selected for rotation. */
  public static final FloatColor ACTIVE_COLOR = FloatColor.RED;
  /** The default width of the created window. */
  public static final int DEFAULT_WINDOW_HEIGHT = 512;
  /** The default height of the created window. */
  public static final int DEFAULT_WINDOW_WIDTH = 512;
  /** The height of the distal joint on each of the limbs. */
  public static final double DISTAL_JOINT_HEIGHT = 0.2;
  /** The radius of each joint which comprises the limb. */
  public static final double LIMB_RADIUS = 0.09;
  /** The radius of the body. */
  public static final double BODY_RADIUS = 0.5;
  /** The color for components which are not selected for rotation. */
  public static final FloatColor INACTIVE_COLOR = FloatColor.ORANGE;
  /** The initial position of the top level component in the scene. */
  public static final Point3D INITIAL_POSITION = new Point3D(0, 0, 0);
  /** The height of the middle joint on each of the limbs. */
  public static final double MIDDLE_JOINT_HEIGHT = 0.25;
  /** The height of the palm joint on each of the limbs. */
  public static final double LIMB_JOINT_HEIGHT = 0.25;
  /** The angle by which to rotate the joint on user request to rotate. */
  public static final double ROTATION_ANGLE = 2.0;
  /** Randomly generated serial version UID. */
  private static final long serialVersionUID = -7060944143920496524L;

  /**
   * Runs the hand simulation in a single JFrame.
   * 
   * @param args
   *          This parameter is ignored.
   */
  public static void main(final String[] args) {
    new PA2().animator.start();
  }

  /**
   * The animator which controls the framerate at which the canvas is animated.
   */
  final FPSAnimator animator;
  /** The canvas on which we draw the scene. */
  private final GLCanvas canvas;
  /** The capabilities of the canvas. */
  private final GLCapabilities capabilities = new GLCapabilities(null);
  /** The legs on the body to be modeled. */
  private final Leg[] legs;
  /** The middle body to be modeled. */
  private final Component middleBody;
  /** The OpenGL utility object. */
  private final GLU glu = new GLU();
  /** The OpenGL utility toolkit object. */
  private final GLUT glut = new GLUT();
  /** The left body to be modeled. */
  private final Component leftBody;
  /** The last x and y coordinates of the mouse press. */
  private int last_x = 0, last_y = 0;
  /** Whether the world is being rotated. */
  private boolean rotate_world = false;
  /** The axis around which to rotate the selected joints. */
  private Axis selectedAxis = Axis.Z;
  /** The set of components which are currently selected for rotation. */
  private final Set<Component> selectedComponents = new HashSet<Component>(18);
  /**
   * The set of fingers which have been selected for rotation.
   * 
   * Selecting a joint will only affect the joints in this set of selected
   * fingers.
   **/
  private final Set<Leg> selectedLegs = new HashSet<Leg>(5);
  /** Whether the state of the model has been changed. */
  private boolean stateChanged = true;
  /**
   * The top level component in the scene which controls the positioning and
   * rotation of everything in the scene.
   */
  private final Component topLevelComponent;
  /** The right body to be modeled. */
  private final Component rightBody;
  /** The quaternion which controls the rotation of the world. */
  private Quaternion viewing_quaternion = new Quaternion();
  /** The set of all components. */
  private final List<Component> components;

  private boolean legside = false;

  public static String INDEX_LIMB_NAME = "index limb";
  public static String INDEX_MIDDLE_NAME = "index middle";
  public static String INDEX_DISTAL_NAME = "index distal";
  public static String RING_LIMB_NAME = "ring limb";
  public static String RING_MIDDLE_NAME = "ring middle";
  public static String RING_DISTAL_NAME = "ring distal";
  public static String MIDDLE_LIMB_NAME = "middle limb";
  public static String MIDDLE_MIDDLE_NAME = "middle middle";
  public static String MIDDLE_DISTAL_NAME = "middle distal";
  public static String PINKY_LIMB_NAME = "pinky limb";
  public static String PINKY_MIDDLE_NAME = "pinky middle";
  public static String PINKY_DISTAL_NAME = "pinky distal";
  public static String CLAW_NAME = "claw";
  public static String CLAW_MIDDLE_NAME = "claw middle";
  public static String CLAW_DISTAL_NAME = "claw distal";
  public static String LEFT_BODY_NAME = "left body";
  public static String MIDDLE_BODY_NAME = "middle body";
  public static String RIGHT_BODY_NAME = "right body";
  public static String TOP_LEVEL_NAME = "top level";

  /**
   * Initializes the necessary OpenGL objects and adds a canvas to this JFrame.
   */
  public PA2() {
    this.capabilities.setDoubleBuffered(true);

    this.canvas = new GLCanvas(this.capabilities);
    this.canvas.addGLEventListener(this);
    this.canvas.addMouseListener(this);
    this.canvas.addMouseMotionListener(this);
    this.canvas.addKeyListener(this);
    // this is true by default, but we just add this line to be explicit
    this.canvas.setAutoSwapBufferMode(true);
    this.getContentPane().add(this.canvas);

    // refresh the scene at 60 frames per second
    this.animator = new FPSAnimator(this.canvas, 60);

    this.setTitle("CS480/CS680 : Hand Simulator");
    this.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    // all the distal joints
    final Component distal1 = new Component(new Point3D(0, 0,
        MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), PINKY_DISTAL_NAME);
    final Component distal2 = new Component(new Point3D(0, 0,
        MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), RING_DISTAL_NAME);
    final Component distal3 = new Component(new Point3D(0, 0,
        MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), MIDDLE_DISTAL_NAME);
    final Component distal4 = new Component(new Point3D(0, 0,
        MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), INDEX_DISTAL_NAME);
    final Component distal5 = new Component(new Point3D(0, 0,
        MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), CLAW_DISTAL_NAME);
    final Component distal6 = new Component(new Point3D(0, 0,
            MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            DISTAL_JOINT_HEIGHT, this.glut), PINKY_DISTAL_NAME);
    final Component distal7 = new Component(new Point3D(0, 0,
            MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            DISTAL_JOINT_HEIGHT, this.glut), RING_DISTAL_NAME);
    final Component distal8 = new Component(new Point3D(0, 0,
            MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            DISTAL_JOINT_HEIGHT, this.glut), MIDDLE_DISTAL_NAME);
    final Component distal9 = new Component(new Point3D(0, 0,
            MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            DISTAL_JOINT_HEIGHT, this.glut), INDEX_DISTAL_NAME);
    final Component distal10 = new Component(new Point3D(0, 0,
            MIDDLE_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            DISTAL_JOINT_HEIGHT, this.glut), CLAW_DISTAL_NAME);

    // all the middle joints
    final Component middle1 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        MIDDLE_JOINT_HEIGHT, this.glut), PINKY_MIDDLE_NAME);
    final Component middle2 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        MIDDLE_JOINT_HEIGHT, this.glut), RING_MIDDLE_NAME);
    final Component middle3 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        MIDDLE_JOINT_HEIGHT, this.glut), MIDDLE_MIDDLE_NAME);
    final Component middle4 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        MIDDLE_JOINT_HEIGHT, this.glut), INDEX_MIDDLE_NAME);
    final Component middle5 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
        MIDDLE_JOINT_HEIGHT, this.glut), CLAW_MIDDLE_NAME);
    final Component middle6 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            MIDDLE_JOINT_HEIGHT, this.glut), PINKY_MIDDLE_NAME);
    final Component middle7 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            MIDDLE_JOINT_HEIGHT, this.glut), RING_MIDDLE_NAME);
    final Component middle8 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            MIDDLE_JOINT_HEIGHT, this.glut), MIDDLE_MIDDLE_NAME);
    final Component middle9 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            MIDDLE_JOINT_HEIGHT, this.glut), INDEX_MIDDLE_NAME);
    final Component middle10 = new Component(new Point3D(0, 0,
            LIMB_JOINT_HEIGHT), new RoundedCylinder(LIMB_RADIUS,
            MIDDLE_JOINT_HEIGHT, this.glut), CLAW_MIDDLE_NAME);

    // all the limb joints, displaced by various amounts from the limb
    final Component limb1 = new Component(new Point3D(-0.3, 0, 0.7),
        new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            PINKY_LIMB_NAME);
    final Component limb2 = new Component(new Point3D(-.1, 0, 0.9),
        new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            RING_LIMB_NAME);
    final Component limb3 = new Component(new Point3D(0.1, 0, 0.95),
        new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            MIDDLE_LIMB_NAME);
    final Component limb4 = new Component(new Point3D(0.3, 0, 0.75),
        new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            INDEX_LIMB_NAME);
    final Component claw1 = new Component(new Point3D(0.24, 0, 0.23),
        new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            CLAW_NAME);
    final Component limb6 = new Component(new Point3D(-0.3, 0, -0.7),
            new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            PINKY_LIMB_NAME);
    final Component limb7 = new Component(new Point3D(-.1, 0, -0.9),
            new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            RING_LIMB_NAME);
    final Component limb8 = new Component(new Point3D(0.1, 0, -0.95),
            new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            MIDDLE_LIMB_NAME);
    final Component limb9 = new Component(new Point3D(0.3, 0, -0.75),
            new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            INDEX_LIMB_NAME);
    final Component claw2 = new Component(new Point3D(0.24, 0, -0.23),
            new RoundedCylinder(LIMB_RADIUS, LIMB_JOINT_HEIGHT, this.glut),
            CLAW_NAME);
    final Component eye1 = new Component(new Point3D(0.24, 0, 0.23),
            new eyeball(LIMB_RADIUS *2, this.glut),
            CLAW_NAME);
    Component lefteye = new Component(new Point3D(0,0,0.23),new eyeball(LIMB_RADIUS,this.glut), CLAW_DISTAL_NAME);
    eye1.addChild(lefteye);
    final Component eye2 = new Component(new Point3D(0.24, 0, -0.23),
            new eyeball(LIMB_RADIUS *2, this.glut),
            CLAW_NAME);
    Component righteye = new Component(new Point3D(0,0,0.23),new eyeball(LIMB_RADIUS,this.glut), CLAW_DISTAL_NAME);
    eye2.addChild(righteye);

    // put together limbs for easier selection by keyboard input later on
    this.legs = new Leg[] { new Leg(limb1, middle1, distal1),
        new Leg(limb2, middle2, distal2),
        new Leg(limb3, middle3, distal3),
        new Leg(limb4, middle4, distal4),
        new Leg(claw1, middle5, distal5),
            new Leg(limb6, middle6, distal6),
            new Leg(limb7, middle7, distal7),
            new Leg(limb8, middle8, distal8),
            new Leg(limb9, middle9, distal9),
            new Leg(claw2, middle10, distal10),};

    // the left body, which models the left joint
    this.leftBody = new Component(new Point3D(0, 0, 0), new Limb(
            BODY_RADIUS, this.glut), LEFT_BODY_NAME);

    // the middle, which models the middle joint
    this.middleBody = new Component(new Point3D(0,0,-1), new Limb(BODY_RADIUS, this.glut), LEFT_BODY_NAME);

    // the right body which models the right joint
    this.rightBody = new Component(new Point3D(0,0,-0.5), new Limb(BODY_RADIUS, this.glut), LEFT_BODY_NAME);

    this.leftBody.addChildren(middleBody, rightBody);
    // the top level component which provides an initial position and rotation
    // to the scene (but does not cause anything to be drawn)
    this.topLevelComponent = new Component(INITIAL_POSITION, TOP_LEVEL_NAME);

    //this.topLevelComponent.addChild(this.hand);
    this.topLevelComponent.addChild(leftBody);

    // the left body's connected to the...left legs
    this.leftBody.addChildren(limb1, limb2, limb3, limb4, claw1);
    this.leftBody.addChildren(limb6, limb7, limb8, limb9, claw2);
    this.leftBody.addChildren(eye1, eye2);
    limb1.addChild(middle1);
    limb2.addChild(middle2);
    limb3.addChild(middle3);
    limb4.addChild(middle4);
    claw1.addChild(middle5);
    middle1.addChild(distal1);
    middle2.addChild(distal2);
    middle3.addChild(distal3);
    middle4.addChild(distal4);
    limb6.addChild(middle6);
    limb7.addChild(middle7);
    limb8.addChild(middle8);
    limb9.addChild(middle9);
    claw2.addChild(middle10);
    middle6.addChild(distal6);
    middle7.addChild(distal7);
    middle8.addChild(distal8);
    middle9.addChild(distal9);


    this.leftBody.rotate(Axis.Y,-90);
    middle1.rotate(Axis.X, 50);
    middle2.rotate(Axis.X, 50);
    middle3.rotate(Axis.X, 50);
    middle4.rotate(Axis.X, 50);
    limb6.rotate(Axis.Y, -180);
    limb7.rotate(Axis.Y, -180);
    limb8.rotate(Axis.Y, -180);
    limb9.rotate(Axis.Y, -180);
    middle6.rotate(Axis.X, 50);
    middle7.rotate(Axis.X, 50);
    middle8.rotate(Axis.X, 50);
    middle9.rotate(Axis.X, 50);

    eye2.rotate(Axis.Y, 90);
    eye1.rotate(Axis.Y, 90);
    claw1.rotate(Axis.X, 90);
    claw2.rotate(Axis.X, 90);
    
    // set rotation limits for the right body
    this.rightBody.setXPositiveExtent(0);
    this.rightBody.setXNegativeExtent(0);
    this.rightBody.setYPositiveExtent(0);
    this.rightBody.setYNegativeExtent(0);
    this.rightBody.setZPositiveExtent(0);
    this.rightBody.setZNegativeExtent(0);

    // set rotation limits for the middle body
    this.middleBody.setXPositiveExtent(0);
    this.middleBody.setXNegativeExtent(0);
    this.middleBody.setYPositiveExtent(0);
    this.middleBody.setYNegativeExtent(0);
    this.middleBody.setZPositiveExtent(0);
    this.middleBody.setZNegativeExtent(0);

    // set rotation limits for the left body
    this.leftBody.setXPositiveExtent(90);
    this.leftBody.setXNegativeExtent(-90);
    this.leftBody.setYPositiveExtent(-80);
    this.leftBody.setYNegativeExtent(-100);
    this.leftBody.setZPositiveExtent(10);
    this.leftBody.setZNegativeExtent(-10);

    // set rotation limits for the limb joints of the limbs
    for (final Component limbJoint : Arrays.asList(limb1, limb2, limb3, limb4)) {
      limbJoint.setXPositiveExtent(30);
      limbJoint.setXNegativeExtent(-15);
      limbJoint.setYPositiveExtent(10);
      limbJoint.setYNegativeExtent(-10);
      limbJoint.setZPositiveExtent(0);
      limbJoint.setZNegativeExtent(0);
    }

    for (final Component limbJoint : Arrays.asList(limb6, limb7, limb8, limb9)) {
      limbJoint.setXPositiveExtent(15);
      limbJoint.setXNegativeExtent(-30);
      limbJoint.setYPositiveExtent(-170);
      limbJoint.setYNegativeExtent(-190);
      limbJoint.setZPositiveExtent(0);
      limbJoint.setZNegativeExtent(0);
    }

    // and set the rotation limits for the claw joint of the claw
    claw1.setXPositiveExtent(100);
    claw1.setXNegativeExtent(90);
    claw1.setYPositiveExtent(30);
    claw1.setYNegativeExtent(0);
    claw1.setZPositiveExtent(0);
    claw1.setZNegativeExtent(0);

    claw2.setXPositiveExtent(90);
    claw2.setXNegativeExtent(80);
    claw2.setYPositiveExtent(30);
    claw2.setYNegativeExtent(0);
    claw2.setZPositiveExtent(0);
    claw2.setZNegativeExtent(0);

    // set rotation limits for the middle joints of the limbs
    for (final Component middleJoint : Arrays.asList(middle1, middle2,
        middle3, middle4, middle6, middle7, middle8, middle9)) {
      middleJoint.setXPositiveExtent(100);
      middleJoint.setXNegativeExtent(0);
      middleJoint.setYPositiveExtent(0);
      middleJoint.setYNegativeExtent(0);
      middleJoint.setZPositiveExtent(0);
      middleJoint.setZNegativeExtent(0);
    }

    for (final Component middleJoint : Arrays.asList(middle5)) {
      middleJoint.setXPositiveExtent(15);
      middleJoint.setXNegativeExtent(0);
      middleJoint.setYPositiveExtent(0);
      middleJoint.setYNegativeExtent(0);
      middleJoint.setZPositiveExtent(0);
      middleJoint.setZNegativeExtent(0);
    }

    for (final Component middleJoint : Arrays.asList(middle10)) {
      middleJoint.setXPositiveExtent(0);
      middleJoint.setXNegativeExtent(-15);
      middleJoint.setYPositiveExtent(0);
      middleJoint.setYNegativeExtent(0);
      middleJoint.setZPositiveExtent(0);
      middleJoint.setZNegativeExtent(0);
    }



    // set rotation limits for the distal joints of the limb
    for (final Component distalJoint : Arrays.asList(distal1, distal2,
        distal3, distal4, distal5, distal6, distal7, distal8, distal9,distal10)) {
      distalJoint.setXPositiveExtent(70);
      distalJoint.setXNegativeExtent(-5);
      distalJoint.setYPositiveExtent(0);
      distalJoint.setYNegativeExtent(0);
      distalJoint.setZPositiveExtent(0);
      distalJoint.setZNegativeExtent(0);
    }

    // create the list of all the components for debugging purposes
    this.components = Arrays.asList(limb1, middle1, distal1, limb2, middle2,
        distal2, limb3, middle3, distal3, limb4, middle4, distal4, claw1,
        middle5, distal5,limb6, middle6, distal6, limb7, middle7,
            distal7, limb8, middle8, distal8, limb9, middle9, distal9, claw2,
            middle10, distal10, this.leftBody, this.middleBody, this.rightBody, lefteye, righteye);
  }

  /**
   * Redisplays the scene containing the hand model.
   * 
   * @param drawable
   *          The OpenGL drawable object with which to create OpenGL models.
   */
  public void display(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // clear the display
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // from here on affect the model view
    gl.glMatrixMode(GL2.GL_MODELVIEW);

    // start with the identity matrix initially
    gl.glLoadIdentity();

    // rotate the world by the appropriate rotation quaternion
    gl.glMultMatrixf(this.viewing_quaternion.toMatrix(), 0);

    // update the position of the components which need to be updated
    // TODO only need to update the selected and JUST deselected components
    if (this.stateChanged) {
      this.topLevelComponent.update(gl);
      this.stateChanged = false;
    }

    // redraw the components
    this.topLevelComponent.draw(gl);
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param drawable
   *          This parameter is ignored.
   * @param modeChanged
   *          This parameter is ignored.
   * @param deviceChanged
   *          This parameter is ignored.
   */
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged) {
    // intentionally unimplemented
  }

  /**
   * Initializes the scene and model.
   * 
   * @param drawable
   *          {@inheritDoc}
   */
  public void init(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // perform any initialization needed by the hand model
    this.topLevelComponent.initialize(gl);

    // initially draw the scene
    this.topLevelComponent.update(gl);

    // set up for shaded display of the hand
    final float light0_position[] = { 1, 1, 1, 0 };
    final float light0_ambient_color[] = { 0.25f, 0.25f, 0.25f, 1 };
    final float light0_diffuse_color[] = { 1, 1, 1, 1 };

    gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glShadeModel(GL2.GL_SMOOTH);

    // set up the light source
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

    // turn lighting and depth buffering on
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);
  }

  /**
   * Interprets key presses according to the following scheme:
   * 
   * up-arrow, down-arrow: increase/decrease rotation angle
   * 
   * @param key
   *          The key press event object.
   */
  public void keyPressed(final KeyEvent key) {
    switch (key.getKeyCode()) {
    case KeyEvent.VK_KP_UP:
    case KeyEvent.VK_UP:
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, ROTATION_ANGLE);
      }
      this.stateChanged = true;
      break;
    case KeyEvent.VK_KP_DOWN:
    case KeyEvent.VK_DOWN:
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, -ROTATION_ANGLE);
      }
      this.stateChanged = true;
      break;
    default:
      break;
    }
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param key
   *          This parameter is ignored.
   */
  public void keyReleased(final KeyEvent key) {
    // intentionally unimplemented
  }

  private final TestCases testCases = new TestCases();

  private void setModelState(final Map<String, Angled> state) {
    //this.upperArm.setAngles(state.get(UPPER_ARM_NAME));
    //this.forearm.setAngles(state.get(FOREARM_NAME));
    //this.hand.setAngles(state.get(HAND_NAME));
    this.legs[0].palmJoint().setAngles(state.get(PINKY_LIMB_NAME));
    this.legs[0].middleJoint().setAngles(state.get(PINKY_MIDDLE_NAME));
    this.legs[0].distalJoint().setAngles(state.get(PINKY_DISTAL_NAME));
    this.legs[1].palmJoint().setAngles(state.get(RING_LIMB_NAME));
    this.legs[1].middleJoint().setAngles(state.get(RING_MIDDLE_NAME));
    this.legs[1].distalJoint().setAngles(state.get(RING_DISTAL_NAME));
    this.legs[2].palmJoint().setAngles(state.get(MIDDLE_LIMB_NAME));
    this.legs[2].middleJoint().setAngles(state.get(MIDDLE_MIDDLE_NAME));
    this.legs[2].distalJoint().setAngles(state.get(MIDDLE_DISTAL_NAME));
    this.legs[3].palmJoint().setAngles(state.get(INDEX_LIMB_NAME));
    this.legs[3].middleJoint().setAngles(state.get(INDEX_MIDDLE_NAME));
    this.legs[3].distalJoint().setAngles(state.get(INDEX_DISTAL_NAME));
    this.legs[4].palmJoint().setAngles(state.get(CLAW_NAME));
    this.legs[4].middleJoint().setAngles(state.get(CLAW_MIDDLE_NAME));
    this.legs[5].palmJoint().setReverseAngles(state.get(PINKY_LIMB_NAME));
    this.legs[5].middleJoint().setAngles(state.get(PINKY_MIDDLE_NAME));
    this.legs[5].distalJoint().setAngles(state.get(PINKY_DISTAL_NAME));
    this.legs[6].palmJoint().setReverseAngles(state.get(RING_LIMB_NAME));
    this.legs[6].middleJoint().setAngles(state.get(RING_MIDDLE_NAME));
    this.legs[6].distalJoint().setAngles(state.get(RING_DISTAL_NAME));
    this.legs[7].palmJoint().setReverseAngles(state.get(MIDDLE_LIMB_NAME));
    this.legs[7].middleJoint().setAngles(state.get(MIDDLE_MIDDLE_NAME));
    this.legs[7].distalJoint().setAngles(state.get(MIDDLE_DISTAL_NAME));
    this.legs[8].palmJoint().setReverseAngles(state.get(INDEX_LIMB_NAME));
    this.legs[8].middleJoint().setAngles(state.get(INDEX_MIDDLE_NAME));
    this.legs[8].distalJoint().setAngles(state.get(INDEX_DISTAL_NAME));
    this.legs[9].palmJoint().setClawAngles(state.get(CLAW_NAME));
    this.legs[9].middleJoint().setAngles(state.get(CLAW_MIDDLE_NAME));
    
    this.stateChanged = true;
  }

  /**
   * Interprets typed keys according to the following scheme:
   * 
   * 1 : toggle the first finger (thumb) active in rotation
   * 
   * 2 : toggle the second finger active in rotation
   * 
   * 3 : toggle the third finger active in rotation
   * 
   * 4 : toggle the fourth finger active in rotation
   * 
   * 5 : toggle the fifth finger active in rotation
   * 
   * 6 : toggle the hand for rotation
   * 
   * 7 : toggle the forearm for rotation
   * 
   * 8 : toggle the upper arm for rotation
   * 
   * X : use the X axis rotation at the active joint(s)
   * 
   * Y : use the Y axis rotation at the active joint(s)
   * 
   * Z : use the Z axis rotation at the active joint(s)
   * 
   * C : resets the hand to the stop sign
   * 
   * P : select joint that connects finger to palm
   * 
   * M : select middle joint
   * 
   * D : select last (distal) joint
   * 
   * R : resets the view to the initial rotation
   * 
   * K : prints the angles of the five fingers for debugging purposes
   * 
   * Q, Esc : exits the program
   * 
   */
  public void keyTyped(final KeyEvent key) {
    switch (key.getKeyChar()) {
    case 'Q':
    case 'q':
    case KeyEvent.VK_ESCAPE:
      new Thread() {
        @Override
        public void run() {
          PA2.this.animator.stop();
        }
      }.start();
      System.exit(0);
      break;

    // print the angles of the components
    case 'K':
    case 'k':
      printJoints();
      break;

    // resets to the stop sign
    case 'C':
    case 'c':
      this.setModelState(this.testCases.stop());
      break;

    // set the state of the hand to the next test case
    case 'T':
    case 't':
      this.setModelState(this.testCases.next());
      break;

    // set the viewing quaternion to 0 rotation
    case 'R':
    case 'r':
      this.viewing_quaternion.reset();
      break;

    // Toggle which finger(s) are affected by the current rotation
    case '1':
      if (legside==false)
        toggleSelection(this.legs[0]);
      else
        toggleSelection(this.legs[5]);
      break;
    case '2':
      if (legside==false)
        toggleSelection(this.legs[1]);
      else
        toggleSelection(this.legs[6]);
      break;
    case '3':
      if (legside==false)
        toggleSelection(this.legs[2]);
      else
        toggleSelection(this.legs[7]);
      break;
    case '4':
      if (legside==false)
        toggleSelection(this.legs[3]);
      else
        toggleSelection(this.legs[8]);
      break;
    case '5':
      if (legside==false)
        toggleSelection(this.legs[4]);
      else
        toggleSelection(this.legs[9]);
      break;


    // toggle which joints are affected by the current rotation
    case 'D':
    case 'd':
      for (final Leg leg : this.selectedLegs) {
        toggleSelection(leg.distalJoint());
      }
      break;
    case 'M':
    case 'm':
      for (final Leg leg : this.selectedLegs) {
        toggleSelection(leg.middleJoint());
      }
      break;
    case 'P':
    case 'p':
      for (final Leg leg : this.selectedLegs) {
        toggleSelection(leg.palmJoint());
      }
      break;

    case '6':
      toggleSelection(this.leftBody);
      break;
    case '7':
      toggleSelection(this.middleBody);
      break;
    case '8':
      toggleSelection(this.rightBody);
      break;
      case '9':
        toggleSelection(this.components.get(components.size()-2));
        break;
      case '0':
        toggleSelection(this.components.get(components.size()-1));
        break;

    // change the axis of rotation at current active joint
    case 'X':
    case 'x':
      this.selectedAxis = Axis.X;
      break;
    case 'Y':
    case 'y':
      this.selectedAxis = Axis.Y;
      break;
    case 'Z':
    case 'z':
      this.selectedAxis = Axis.Z;
      break;
      case 'A':
      case 'a':
        legside = !legside;
    default:
      break;
    }
  }

  /**
   * Prints the joints on the System.out print stream.
   */
  private void printJoints() {
    this.printJoints(System.out);
  }

  /**
   * Prints the joints on the specified PrintStream.
   * 
   * @param printStream
   *          The stream on which to print each of the components.
   */
  private void printJoints(final PrintStream printStream) {
    for (final Component component : this.components) {
      printStream.println(component);
    }
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseClicked(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * Updates the rotation quaternion as the mouse is dragged.
   * 
   * @param mouse
   *          The mouse drag event object.
   */
  public void mouseDragged(final MouseEvent mouse) {
	if (this.rotate_world) {
		// get the current position of the mouse
		final int x = mouse.getX();
		final int y = mouse.getY();
	
		// get the change in position from the previous one
		final int dx = x - this.last_x;
		final int dy = y - this.last_y;
	
		// create a unit vector in the direction of the vector (dy, dx, 0)
		final double magnitude = Math.sqrt(dx * dx + dy * dy);
		final float[] axis = magnitude == 0 ? new float[]{1,0,0}: // avoid dividing by 0
			new float[] { (float) (dy / magnitude),(float) (dx / magnitude), 0 };
	
		// calculate appropriate quaternion
		final float viewing_delta = 3.1415927f / 180.0f;
		final float s = (float) Math.sin(0.5f * viewing_delta);
		final float c = (float) Math.cos(0.5f * viewing_delta);
		final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
				* axis[2]);
		this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
	
		// normalize to counteract acccumulating round-off error
		this.viewing_quaternion.normalize();
	
		// save x, y as last x, y
		this.last_x = x;
		this.last_y = y;
	}
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseEntered(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseExited(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseMoved(MouseEvent mouse) {
    // intentionally unimplemented
    int x = mouse.getX();
    int y = mouse.getY();
    double angle_x = (x - 256.0)/256.0*90.0;
    double angle_y = (y - 256.0)/256.0*90.0;
    this.components.get(components.size()-2).setAngles(angle_y,angle_x,0);
    this.components.get(components.size()-1).setAngles(angle_y,angle_x,0);
    this.stateChanged = true;
  }

  /**
   * Starts rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse press event object.
   */
  public void mousePressed(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.last_x = mouse.getX();
      this.last_y = mouse.getY();
      this.rotate_world = true;
    }
  }

  /**
   * Stops rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse release event object.
   */
  public void mouseReleased(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.rotate_world = false;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @param drawable
   *          {@inheritDoc}
   * @param x
   *          {@inheritDoc}
   * @param y
   *          {@inheritDoc}
   * @param width
   *          {@inheritDoc}
   * @param height
   *          {@inheritDoc}
   */
  public void reshape(final GLAutoDrawable drawable, final int x, final int y,
      final int width, final int height) {
    final GL2 gl = (GL2)drawable.getGL();

    // prevent division by zero by ensuring window has height 1 at least
    final int newHeight = Math.max(1, height);

    // compute the aspect ratio
    final double ratio = (double) width / newHeight;

    // reset the projection coordinate system before modifying it
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // set the viewport to be the entire window
    gl.glViewport(0, 0, width, newHeight);

    // set the clipping volume
    this.glu.gluPerspective(25, ratio, 0.1, 100);

    // camera positioned at (0,0,6), look at point (0,0,0), up vector (0,1,0)
    this.glu.gluLookAt(0, 0, 12, 0, 0, 0, 0, 1, 0);

    // switch back to model coordinate system
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  private void toggleSelection(final Component component) {
    if (this.selectedComponents.contains(component)) {
      this.selectedComponents.remove(component);
      component.setColor(INACTIVE_COLOR);
    } else {
      this.selectedComponents.add(component);
      component.setColor(ACTIVE_COLOR);
    }
    this.stateChanged = true;
  }

  private void toggleSelection(final Leg leg) {
    if (this.selectedLegs.contains(leg)) {
      this.selectedLegs.remove(leg);
      this.selectedComponents.removeAll(leg.joints());
      for (final Component joint : leg.joints()) {
        joint.setColor(INACTIVE_COLOR);
      }
    } else {
      this.selectedLegs.add(leg);
    }
    this.stateChanged = true;
  }

@Override
public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub
	
}
}
