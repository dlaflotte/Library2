// Copyright (c) 2026 Team 2342
// https://github.com/FRCTeamPhoenix
//
// This source code is licensed under the MIT License.
// See the LICENSE file in the root directory of this project.

package org.team2342.lib.leds;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.team2342.lib.leds.LedIO.Animation;
import org.team2342.lib.leds.LedIO.LedIOInputs;

/**
 * LED strip subsystem providing high-level control over LED animations.
 *
 * <p>Manages up to 8 independent LED sections, each capable of running different animations
 * simultaneously. Integrates with AdvantageKit for logging and uses the IO layer pattern
 * for hardware abstraction.
 *
 * <p><b>Basic Usage:</b>
 * <pre>
 * // Create subsystem (typically in RobotContainer)
 * LedStrip leds = new LedStrip(new LedIOCANdle(22, 300), "LEDs");
 *
 * // Set individual sections
 * leds.setSection(0, Animation.SOLID, Color.kRed);
 * leds.setSection(1, Animation.RAINBOW, Color.kBlack);
 * leds.setSection(2, Animation.STROBE, Color.kBlue);
 *
 * // Set all sections at once
 * leds.setAll(Animation.SOLID, Color.kGreen);
 *
 * // Use convenience methods
 * leds.setAlternating(Animation.SOLID, Color.kRed, Color.kBlue);
 * </pre>
 *
 * <p><b>Advanced Features:</b>
 * <ul>
 *   <li>Control speed and brightness per section
 *   <li>Create alternating color patterns
 *   <li>Implement chase/scanner effects
 *   <li>Full AdvantageKit logging integration
 * </ul>
 */
public class LedStrip extends SubsystemBase {
  private final LedIO io;
  private final LedIOInputs inputs = new LedIOInputs();

  /**
   * Creates a new LED strip subsystem.
   *
   * @param io The LED IO implementation (LedIOCANdle for hardware, LedIOSim for simulation)
   * @param name Subsystem name used for AdvantageKit logging
   */
  public LedStrip(LedIO io, String name) {
    this.io = io;
    setName(name);

    // Initialize inputs array
    for (int i = 0; i < 8; i++) {
      inputs.sections[i] = new LedIO.LedSection();
    }
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    // Logger.processInputs(getName(), inputs);
  }

  /**
   * Set a specific section to an animation and color
   *
   * @param section Section number (0-7)
   * @param animation Animation type
   * @param color Primary color for the animation
   */
  public void setSection(int section, Animation animation, Color color) {
    io.setSection(section, animation, color);
  }

  /**
   * Set a specific section with primary and secondary colors
   *
   * @param section Section number (0-7)
   * @param animation Animation type
   * @param primaryColor Primary color
   * @param secondaryColor Secondary color (used by some animations)
   */
  public void setSection(int section, Animation animation, Color primaryColor, Color secondaryColor) {
    io.setSection(section, animation, primaryColor, secondaryColor);
  }

  /**
   * Set all sections to the same animation and color
   *
   * @param animation Animation type
   * @param color Color for the animation
   */
  public void setAll(Animation animation, Color color) {
    io.setAll(animation, color);
  }

  /** Turns off all LEDs and clears all animations across all sections. */
  public void clearAll() {
    io.clearAll();
  }

  /**
   * Sets the animation speed for a specific section (hardware only).
   *
   * @param section Section number (0-7)
   * @param speed Speed multiplier (0.0-1.0, where 1.0 is normal speed)
   */
  public void setSectionSpeed(int section, double speed) {
    if (io instanceof LedIOCANdle) {
      ((LedIOCANdle) io).setSectionSpeed(section, speed);
    }
  }

  /**
   * Sets the LED brightness for a specific section (hardware only).
   *
   * @param section Section number (0-7)
   * @param brightness Brightness level (0.0-1.0, where 1.0 is full brightness)
   */
  public void setSectionBrightness(int section, double brightness) {
    if (io instanceof LedIOCANdle) {
      ((LedIOCANdle) io).setSectionBrightness(section, brightness);
    }
  }

  /**
   * Gets the LED index range for a specific section.
   *
   * @param section Section number (0-7)
   * @return Array with [startIndex, endIndex], or [0, 0] if not supported
   */
  public int[] getSectionRange(int section) {
    if (io instanceof LedIOCANdle) {
      return ((LedIOCANdle) io).getSectionRange(section);
    }
    return new int[] {0, 0};
  }

  // ==================== Convenience Methods ====================

  /**
   * Sets a range of consecutive sections to the same animation and color.
   *
   * @param startSection First section (inclusive, 0-7)
   * @param endSection Last section (inclusive, 0-7)
   * @param animation Animation type
   * @param color Color for all sections in range
   */
  public void setSections(int startSection, int endSection, Animation animation, Color color) {
    for (int i = startSection; i <= endSection && i < 8; i++) {
      setSection(i, animation, color);
    }
  }

  /**
   * Sets alternating sections to two different colors.
   *
   * <p>Creates an even/odd pattern useful for team colors or visual indicators.
   * Even sections (0, 2, 4, 6) use color1, odd sections (1, 3, 5, 7) use color2.
   *
   * @param animation Animation type applied to all sections
   * @param color1 Color for even-numbered sections
   * @param color2 Color for odd-numbered sections
   */
  public void setAlternating(Animation animation, Color color1, Color color2) {
    for (int i = 0; i < 8; i++) {
      setSection(i, animation, (i % 2 == 0) ? color1 : color2);
    }
  }

  /**
   * Creates a sequential "chase" or "scanner" effect across sections.
   *
   * <p>Lights up one section at a time in sequence. Call this method repeatedly
   * with incrementing step values to create a moving light effect.
   *
   * <p>Example:
   * <pre>
   * int step = 0;
   * // In periodic method:
   * leds.setChase(Animation.SOLID, Color.kRed, step++);
   * </pre>
   *
   * @param animation Animation type for the active section
   * @param color Color for the lit section
   * @param step Current step (wraps around at 8)
   */
  public void setChase(Animation animation, Color color, int step) {
    for (int i = 0; i < 8; i++) {
      if (i == (step % 8)) {
        setSection(i, animation, color);
      } else {
        setSection(i, Animation.OFF, Color.kBlack);
      }
    }
  }
}
