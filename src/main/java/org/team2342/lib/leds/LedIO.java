// Copyright (c) 2026 Team 2342
// https://github.com/FRCTeamPhoenix
//
// This source code is licensed under the MIT License.
// See the LICENSE file in the root directory of this project.

package org.team2342.lib.leds;

import edu.wpi.first.wpilibj.util.Color;

public interface LedIO {
  public static class LedIOInputs {
    public LedSection[] sections = new LedSection[8];
  }

  /** Update loggable inputs */
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

  /** Clear all animations and turn off all LEDs */
  public default void clearAll() {}

  /** Data class representing a single LED section */
  public static class LedSection {
    public int startIndex;
    public int endIndex;
    public Animation animation;
    public Color primaryColor;
    public Color secondaryColor;
    public double speed;
    public double brightness;

    public LedSection() {
      this.animation = Animation.OFF;
      this.primaryColor = Color.kBlack;
      this.secondaryColor = Color.kBlack;
      this.speed = 1.0;
      this.brightness = 1.0;
    }
  }

  /** All available CANdle animation types */
  public enum Animation {
    /** No animation - LEDs off */
    OFF,

    /** Solid color - no animation */
    SOLID,

    /** Color flow animation - colors flow along the strip */
    COLOR_FLOW,

    /** Fire animation - flickering fire effect */
    FIRE,

    /** Larson scanner animation - KITT/Cylon effect */
    LARSON,

    /** Rainbow animation - rotating rainbow */
    RAINBOW,

    /** RGB fade animation - fade through RGB colors */
    RGB_FADE,

    /** Single fade animation - fade in/out of a single color */
    SINGLE_FADE,

    /** Strobe animation - rapid flashing */
    STROBE,

    /** Twinkle animation - random twinkling LEDs */
    TWINKLE,

    /** Twinkle off animation - random dimming LEDs */
    TWINKLE_OFF
  }
}
