// Copyright (c) 2026 Team 2342
// https://github.com/FRCTeamPhoenix
//
// This source code is licensed under the MIT License.
// See the LICENSE file in the root directory of this project.

package org.team2342.lib.leds;

import edu.wpi.first.wpilibj.util.Color;

/**
 * Hardware abstraction interface for LED controllers.
 *
 * <p>Supports up to 8 independent LED sections, each with its own animation and colors.
 * Implementations include hardware (CANdle) and simulation.
 */
public interface LedIO {
  /** Container for logged LED state data */
  public static class LedIOInputs {
    public LedSection[] sections = new LedSection[8];
  }

  /**
   * Update logged inputs with current LED state.
   * Called periodically by the subsystem for AdvantageKit logging.
   */
  public default void updateInputs(LedIOInputs inputs) {}

  /**
   * Set a specific section to a color and animation
   *
   * @param section Section number (0-7)
   * @param animation Animation type
   * @param color Primary color
   */
  public default void setSection(int section, Animation animation, Color color) {}

  /**
   * Set a specific section to a color and animation with a secondary color
   *
   * @param section Section number (0-7)
   * @param animation Animation type
   * @param color Primary color
   * @param secondColor Secondary color (used by some animations)
   */
  public default void setSection(int section, Animation animation, Color color, Color secondColor) {}

  /**
   * Set all sections to the same animation and color
   *
   * @param animation Animation type
   * @param color Primary color
   */
  public default void setAll(Animation animation, Color color) {}

  /**
   * Clear all animations and turn off all LEDs.
   * Sets all sections to OFF with black color.
   */
  public default void clearAll() {}

  /**
   * Configuration for a single LED section.
   * Each section can have independent animation, colors, speed, and brightness.
   */
  public static class LedSection {
    /** Starting LED index in the strip (inclusive) */
    public int startIndex;
    /** Ending LED index in the strip (exclusive) */
    public int endIndex;
    /** Current animation running on this section */
    public Animation animation;
    /** Primary color (used by all animations) */
    public Color primaryColor;
    /** Secondary color (used by some animations like COLOR_FLOW) */
    public Color secondaryColor;
    /** Animation speed (0.0-1.0, where 1.0 is normal speed) */
    public double speed;
    /** LED brightness (0.0-1.0, where 1.0 is full brightness) */
    public double brightness;

    public LedSection() {
      this.animation = Animation.OFF;
      this.primaryColor = Color.kBlack;
      this.secondaryColor = Color.kBlack;
      this.speed = 1.0;
      this.brightness = 1.0;
    }
  }

  /**
   * Available LED animation types.
   * All animations are hardware-accelerated on the CANdle device.
   */
  public enum Animation {
    /** No animation - all LEDs off (black) */
    OFF,

    /** Static solid color with no animation */
    SOLID,

    /** Flowing color pattern that moves along the strip */
    COLOR_FLOW,

    /** Flickering fire effect with random intensity variation */
    FIRE,

    /** "KITT" or "Cylon" scanner effect that bounces back and forth */
    LARSON,

    /** Full spectrum rainbow that rotates through the strip */
    RAINBOW,

    /** Smooth fade through red, green, and blue colors */
    RGB_FADE,

    /** Fade in and out of a single specified color */
    SINGLE_FADE,

    /** Rapid on/off flashing at high speed */
    STROBE,

    /** Random LEDs twinkle on at varying brightness levels */
    TWINKLE,

    /** Random LEDs dim/turn off (inverse of TWINKLE) */
    TWINKLE_OFF
  }
}
