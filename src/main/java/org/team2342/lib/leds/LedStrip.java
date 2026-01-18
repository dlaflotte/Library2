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
 * LED strip subsystem that manages up to 8 independent sections.
 * Each section can display a different animation simultaneously.
 *
 * <p>Example usage:
 * <pre>
 * // Set section 0 to solid red
 * ledStrip.setSection(0, Animation.SOLID, Color.kRed);
 *
 * // Set section 1 to rainbow
 * ledStrip.setSection(1, Animation.RAINBOW, Color.kBlack);
 *
 * // Set section 2 to blue strobe
 * ledStrip.setSection(2, Animation.STROBE, Color.kBlue);
 *
 * // Set all sections to solid green
 * ledStrip.setAll(Animation.SOLID, Color.kGreen);
 * </pre>
 */
public class LedStrip extends SubsystemBase {
  private final LedIO io;
  private final LedIOInputs inputs = new LedIOInputs();

  /**
   * Create a new LED strip subsystem
   *
   * @param io The LED IO implementation (typically LedIOCANdle)
   * @param name Subsystem name for logging
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

  /**
   * Turn off all LEDs and clear all animations
   */
  public void clearAll() {
    io.clearAll();
  }

  /**
   * Set animation speed for a specific section
   *
   * @param section Section number (0-7)
   * @param speed Speed value (0.0 to 1.0, where 1.0 is fastest)
   */
  public void setSectionSpeed(int section, double speed) {
    if (io instanceof LedIOCANdle) {
      ((LedIOCANdle) io).setSectionSpeed(section, speed);
    }
  }

  /**
   * Set animation brightness for a specific section
   *
   * @param section Section number (0-7)
   * @param brightness Brightness value (0.0 to 1.0, where 1.0 is brightest)
   */
  public void setSectionBrightness(int section, double brightness) {
    if (io instanceof LedIOCANdle) {
      ((LedIOCANdle) io).setSectionBrightness(section, brightness);
    }
  }

  /**
   * Get the LED index range for a section
   *
   * @param section Section number (0-7)
   * @return Array with [startIndex, endIndex]
   */
  public int[] getSectionRange(int section) {
    if (io instanceof LedIOCANdle) {
      return ((LedIOCANdle) io).getSectionRange(section);
    }
    return new int[] {0, 0};
  }

  // Convenience methods for common patterns

  /**
   * Set multiple sections to the same animation
   *
   * @param startSection First section (inclusive)
   * @param endSection Last section (inclusive)
   * @param animation Animation type
   * @param color Color for the animation
   */
  public void setSections(int startSection, int endSection, Animation animation, Color color) {
    for (int i = startSection; i <= endSection && i < 8; i++) {
      setSection(i, animation, color);
    }
  }

  /**
   * Set alternating sections to two different colors
   * Useful for team color patterns
   *
   * @param animation Animation type for all sections
   * @param color1 First color
   * @param color2 Second color
   */
  public void setAlternating(Animation animation, Color color1, Color color2) {
    for (int i = 0; i < 8; i++) {
      setSection(i, animation, (i % 2 == 0) ? color1 : color2);
    }
  }

  /**
   * Create a "chase" effect by setting sections in sequence
   * Call this repeatedly with incrementing step values
   *
   * @param animation Animation type
   * @param color Color for the lit section
   * @param step Current step (0-7)
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
