// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.lib.SwerveModule;

public class Drivetrain extends SubsystemBase {
  /** Creates a new Drivetrain. */

  private final Translation2d m_frontLeftLocation = new Translation2d(Constants.LENGTH_TO_WHEELS, Constants.WIDTH_TO_WHEELS);
  private final Translation2d m_frontRightLocation = new Translation2d(Constants.LENGTH_TO_WHEELS, -Constants.WIDTH_TO_WHEELS);
  private final Translation2d m_backLeftLocation = new Translation2d(-Constants.LENGTH_TO_WHEELS, Constants.WIDTH_TO_WHEELS);
  private final Translation2d m_backRightLocation = new Translation2d(-Constants.LENGTH_TO_WHEELS, -Constants.WIDTH_TO_WHEELS);
  
  private final SwerveModule m_frontLeft = new SwerveModule(this, 1, 2);
  private final SwerveModule m_frontRight = new SwerveModule(this, 3, 4);
  private final SwerveModule m_backLeft = new SwerveModule(this, 5, 6);
  private final SwerveModule m_backRight = new SwerveModule(this, 7, 8);
  
  private final AHRS m_gyro = new AHRS(SPI.Port.kMXP); 

  private final SwerveDriveKinematics m_kinematics =
    new SwerveDriveKinematics(
      m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

  private final SwerveDriveOdometry m_odometry =
    new SwerveDriveOdometry(m_kinematics, m_gyro.getRotation2d());

  ShuffleboardTab tab;
  public NetworkTableEntry P, I, D, Iz, FF, MaxOutput, mxRPM, mxVel, mxAcc, Err, P2, I2, D2, Iz2, FF2, MaxOutput2, mxRPM2, mxVel2, mxAcc2, Err2, driveSpeed, turnPos;
  
  public Drivetrain() {
    m_gyro.reset();
    smartdashboardInit();
  }

  @Override
  public void periodic() {
    m_frontLeft.updatePID();
    m_frontRight.updatePID();
    m_backLeft.updatePID();
    m_backRight.updatePID();
  }

  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    var swerveModuleStates =
        m_kinematics.toSwerveModuleStates(
            fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, m_gyro.getRotation2d())
                : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.normalizeWheelSpeeds(swerveModuleStates, Constants.kMaxSpeed);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);
  }

    /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    m_odometry.update(
        m_gyro.getRotation2d(),
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_backLeft.getState(),
        m_backRight.getState());
  }

  public void smartdashboardInit(){
    ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");
    P = tab.addPersistent("P", Constants.kP).getEntry();
    I = tab.addPersistent("I", Constants.kI).getEntry();
    D = tab.addPersistent("D", Constants.kD).getEntry(); 
    Iz = tab.addPersistent("Iz", Constants.kIz).getEntry();
    FF = tab.addPersistent("FF", Constants.kFF).getEntry();
    MaxOutput = tab.addPersistent("Max Output", Constants.kMaxOutput).getEntry();
    mxRPM = tab.addPersistent("Max RPM", Constants.maxRPM).getEntry();
    mxVel = tab.addPersistent("Max Velocity", Constants.maxVel).getEntry();
    mxAcc = tab.addPersistent("Max Acceleration", Constants.maxAcc).getEntry(); 
    Err = tab.addPersistent("Acceptable Error", Constants.ERROR).getEntry();

    P2 = tab.addPersistent("P2", Constants.kP).getEntry();
    I2 = tab.addPersistent("I2", Constants.kI).getEntry();
    D2 = tab.addPersistent("D2", Constants.kD).getEntry(); 
    Iz2 = tab.addPersistent("Iz2", Constants.kIz).getEntry();
    FF2 = tab.addPersistent("FF2", Constants.kFF).getEntry();
    MaxOutput2 = tab.addPersistent("Max Output2", Constants.kMaxOutput).getEntry();
    mxRPM2 = tab.addPersistent("Max RPM2", Constants.maxRPM).getEntry();
    mxVel2 = tab.addPersistent("Max Velocity2", Constants.maxVel).getEntry();
    mxAcc2 = tab.addPersistent("Max Acceleration2", Constants.maxAcc).getEntry(); 
    Err2 = tab.addPersistent("Acceptable Error2", Constants.ERROR).getEntry();
  }
}
