// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PS4Controller.Button;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PS4Controller.Axis;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import java.util.Set;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Robot extends TimedRobot {

    // declare WPI_VictorSPX objects for the four drive motors
    private WPI_VictorSPX m_rearLeft;
    private WPI_VictorSPX m_frontLeft;
    private WPI_VictorSPX m_rearRight;
    private WPI_VictorSPX m_frontRight;

    // declare WPI_VictorSPX object for the arm motors
    private WPI_VictorSPX m_arm;
    private WPI_VictorSPX m_wrist;
    private WPI_VictorSPX m_claw;
    private WPI_VictorSPX m_extend;

    // declare Joystick object for driver control

    private Joystick m_operator;
    private PS4Controller m_driver;
    // declare DifferentialDrive object for tank drive
    private DifferentialDrive m_robotdrive;

    // auto timer
    public int timer = 0;;

    // declare solenoid for pneumatic arm
    private DoubleSolenoid p_arm;

    // boolean keepClawClose = false;

    @Override
    public void robotInit() {

        // Camera Code
        CameraServer.startAutomaticCapture();

        // initialize WPI_VictorSPX objects with CAN ports for drive motors
        m_rearLeft = new WPI_VictorSPX(2);
        m_frontLeft = new WPI_VictorSPX(1);
        m_rearRight = new WPI_VictorSPX(3);
        m_frontRight = new WPI_VictorSPX(4);

        // Sets default mode to break for all motors
        m_frontRight.setNeutralMode(NeutralMode.Brake);
        m_rearRight.setNeutralMode(NeutralMode.Brake);
        m_frontLeft.setNeutralMode(NeutralMode.Brake);
        m_rearLeft.setNeutralMode(NeutralMode.Brake);
        // m_arm.setNeutralMode(NeutralMode.Brake);
        // m_wrist.setNeutralMode(NeutralMode.Brake);
        // m_claw.setNeutralMode(NeutralMode.Brake);
        // m_extend.setNeutralMode(NeutralMode.Brake);

        // initialize WPI_VictorSPX object with CAN ports for arm motors
        m_arm = new WPI_VictorSPX(7);
        m_wrist = new WPI_VictorSPX(5);
        m_claw = new WPI_VictorSPX(6);
        m_extend = new WPI_VictorSPX(8);

        // initialize joystick object with port number
        m_driver = new PS4Controller(0);
        m_operator = new Joystick(1);
        m_rearLeft.setInverted(true);
        m_frontLeft.setInverted(true);

        // initialize motor groups for DifferentialDrive
        m_rearLeft.follow(m_frontLeft);
        m_rearRight.follow(m_frontRight);

        // initialize DifferentialDrive object with four drive motors
        m_robotdrive = new DifferentialDrive(m_frontLeft, m_frontRight);

        // set initial speed for drive motors to 0.6
        m_robotdrive.setMaxOutput(0.6); // < ---------------- Default Drive Speed Value

        // p_arm = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1); // <---- this
        // defines the solenoid and its type as well as what it is set to initially
    }

    // runs periodically when robot is on
    public void robotPeriodic() {
    }

    /////////////////////////////////////
    // -----------Teleop Code-----------//
    /////////////////////////////////////
    @Override
    public void teleopPeriodic() {

        ////////////////////////////////////
        // --------- Driving Code ---------//

        if (m_driver.getL1Button()) // <---- fast speed for drive if left bumper is held down
        {
            // set max when sped up
            m_robotdrive.setMaxOutput(.9);
        }

        else if (m_driver.getR1Button()) // <---- slow speed for drive if right bumper is held down
        {
            // set max when slowed down
            m_robotdrive.setMaxOutput(.32);
        } else {
            // set max for default
            m_robotdrive.setMaxOutput(.5);
        }

        // these take the up/down of one joystick and left/right of other one and
        // converts to arcade drive
        m_robotdrive.arcadeDrive(m_driver.getLeftY(), m_driver.getRawAxis(4));

        ////////////////////////////////////////////////////
        // ------------ Main Arm Motor Code ---------------//
        /* read joystick on FightStick to control arm motor */

        if (m_operator.getRawAxis(1) < 0)
            ; // <------------------forward/upward movement
        {
            m_arm.set(0.395); // move arm up
        }

        if (m_operator.getRawAxis(1) == 0) // <------------------stop movement
        {
            m_arm.set(0.0697); // arm fight gravity
        }

        if (m_operator.getRawAxis(1) > 0) // <------------------backward/downward movement
        {
            m_arm.set(-0.1055); // move arm down
        }

        /*
         * if (m_operator.getRawButton(1)) { //<------------------Button for arm up
         * movement (X)
         * // set arm motor speed to forward
         * m_arm.set(0.4);
         * }
         * else if (m_operator.getRawButton(2)){ //<------------------Button for arm
         * down movement (A)
         * // set arm motor speed to reverse
         * m_arm.set(-0.07);
         * }
         * if(m_operator.getRawButtonReleased(1) || m_operator.getRawButtonReleased(2))
         * {
         * m_arm.set(0);
         * }
         */

        //////////////////////////////////////////////////////
        // -------------- Extend Arm Motor Code -------------//

        /* read button RB and LB on Joystick to control extend motor */
        if (m_operator.getRawAxis(5) < 0) { // <------------------Button for extending arm (RB)
            // set extend motor to forward to extend arm
            m_extend.set(-0.65);
        }
        if (m_operator.getRawAxis(5) > 0) // <------------------Button for retracting arm (LB)
        {
            // set extend motor speed to retract arm
            m_extend.set(0.5);
        }
        if (m_operator.getRawAxis(5) == 0) // <------------------extend stop on release
        {
            m_extend.set(0);
            // m_extend.set(-0.075); //arm extends
        }
        /*
         * 
         * //-------------Alternate Extend (joystick)----------------//
         * if (m_operator.getRawAxis(4) < 0) //<------------------forward/upward
         * movement
         * {
         * m_extend.set(0.5); //move arm up
         * }
         * 
         * if (m_operator.getRawAxis(4) == 0) //<------------------stop movement
         * {
         * m_extend.set(0);
         * }
         * 
         * if (m_operator.getRawAxis(4) > 0) //<------------------backward/downward
         * movement
         * {
         * m_extend.set(-0.5); //move arm down
         * }
         */

        //////////////////////////////////////////////////////
        // ------------- Arm Wrist Motor Code ---------------//
        /* read button A and B on Joystick to control wrist motor */

        if (m_operator.getRawButton(6)) { // <------------------Button for wrist up movement (A)
            // set wrist motor speed to forward lowering wrist
            m_wrist.set(0.2955);
        }
        if (m_operator.getRawButton(5)) // <------------------Button for wrist down movement (B)
        {
            // set wrist motor speed to reverse raising wrist
            m_wrist.set(-0.65);
        }
        if (m_operator.getRawButtonReleased(6) || m_operator.getRawButtonReleased(5)) // <------------------wrist stop
                                                                                      // on release
        {
            m_wrist.set(0); // wrist stops
        }

        /////////////////////////////////////////////////////
        // ------------- Arm Claw Motor Code ---------------//
        /* read button Y and B on Joystick to control claw motor */

        if (m_operator.getRawButton(1)) // <------------------Button for claw close movement (Y)
        {
            // set claw motor speed to forward to close claw
            m_claw.set(0.79);
        }

        if (m_operator.getRawButtonReleased(1)) // <------------------Button for claw close movement (Y) released
        {
            m_claw.set(0.7); // continously close claw
            m_arm.set(0.0825);
            // m_claw.set(0); //stop claw movement
        }

        if (m_operator.getRawButton(2)) // <------------------Button for claw open movement (B)
        {
            // set claw motor speed to open claw
            m_claw.set(-0.25);
        }

        if (m_operator.getRawButtonReleased(2)) // <------------------Button for claw open movement (B) released
        {
            m_claw.set(0); // stop claw movement
            m_arm.set(0);
        }

        //////////////////////////////////////////////////////////
        // --------------- Arm Pneumatics Code ------------------//
        /*
         * if (m_operator.getRawButton(6)) // <---- when pressing "RB", arm moves
         * forward
         * {
         * p_arm.set(DoubleSolenoid.Value.kForward);
         * }
         * 
         * if (m_operator.getRawButton(5)) // <---- when pressing "LB", arm moves
         * backward
         * {
         * p_arm.set(DoubleSolenoid.Value.kReverse);
         * }
         */
    }

    @Override
    public void autonomousInit() {

        timer = 0; // initialize timer with a value of 0

    }

    // runs periodically when robot is disabled
    public void disabledPeriodic() {
    }

    ////////////////////////////////////
    // ---------Autonomous Code--------//
    ////////////////////////////////////
    public void autonomousPeriodic() {

        timer++; // increase timer by one each cycle

        /*
         * //Code to get up ramp
         * if (timer < 70 && timer > 1)
         * {
         * m_frontLeft.set(-0.75);
         * m_frontRight.set(-0.75);
         * }
         */

        // move forward during autonomous
        if (timer < 35 && timer > 1) {
            m_frontLeft.set(-0.75);
            m_frontRight.set(-0.75);
        }

        // move back during autonomous
        else if (timer < 100 && timer > 60) {
            m_frontLeft.set(0.75);
            m_frontRight.set(0.75);
        }
        // move forward during autonomous
        else if (timer < 170 && timer > 120) {
            m_frontLeft.set(-0.75);
            m_frontRight.set(-0.75);
        } else // stop moving in autonomous
        {
            m_frontLeft.set(0);
            m_frontRight.set(0);
        }

    }
}