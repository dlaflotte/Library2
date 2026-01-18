// Copyright (c) 2026 Team 2342
// https://github.com/FRCTeamPhoenix
//
// This source code is licensed under the MIT License.
// See the LICENSE file in the root directory of this project.

package org.team2342.lib.leds;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.FireAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.RgbFadeAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;
import com.ctre.phoenix6.controls.TwinkleOffAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.Enable5VRailValue;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import edu.wpi.first.wpilibj.util.Color;
import org.team2342.frc.util.PhoenixUtils;

/**
 * CANdle LED controller implementation supporting up to 8 independent sections.
 * Each section can run a different animation on its own slot (0-7).
 *
 * <p>Based on CTRE Phoenix 6 CANdle best practices:
 * - Uses separate animation slots for concurrent animations
 * - Only updates when state changes to avoid flashing
 * - Properly clears animations before changing modes
 */
public class LedIOCANdle implements LedIO {
  private final CANdle candle;
  private final LedSection[] sections;
  private final int totalLedCount;

  // Track previous state to avoid unnecessary updates
  private final Animation[] previousAnimations;
  private final Color[] previousPrimaryColors;
  private final Color[] previousSecondaryColors;

  /**
   * Creates a new CANdle LED controller
   *
   * @param canId CAN ID of the CANdle device
   * @param totalLedCount Total number of LEDs in the strip
   * @param canBusName CAN bus name (typically "rio" or "canivore")
   */
  public LedIOCANdle(int canId, int totalLedCount, String canBusName) {
    this.candle = new CANdle(canId, new CANBus(canBusName));
    this.totalLedCount = totalLedCount;
    this.sections = new LedSection[8];
    this.previousAnimations = new Animation[8];
    this.previousPrimaryColors = new Color[8];
    this.previousSecondaryColors = new Color[8];

    // Initialize sections with equal division of LED strip
    int ledsPerSection = totalLedCount / 8;
    for (int i = 0; i < 8; i++) {
      sections[i] = new LedSection();
      sections[i].startIndex = i * ledsPerSection;
      sections[i].endIndex = (i + 1) * ledsPerSection;
      sections[i].animation = Animation.OFF;
      sections[i].primaryColor = Color.kBlack;
      sections[i].secondaryColor = Color.kBlack;
      sections[i].speed = 1.0;
      sections[i].brightness = 1.0;

      previousAnimations[i] = Animation.OFF;
      previousPrimaryColors[i] = Color.kBlack;
      previousSecondaryColors[i] = Color.kBlack;
    }

    // Configure CANdle hardware
    CANdleConfiguration config = new CANdleConfiguration();
    config.LED.StripType = StripTypeValue.GRB;
    config.LED.BrightnessScalar = 0.7;
    config.CANdleFeatures.Enable5VRail = Enable5VRailValue.Enabled;
    candle.getConfigurator().apply(config);

    // Initialize all sections to off
    clearAll();
  }

  /**
   * Convenience constructor using default "rio" CAN bus
   */
  public LedIOCANdle(int canId, int totalLedCount) {
    this(canId, totalLedCount, "rio");
  }

  @Override
  public void updateInputs(LedIOInputs inputs) {
    // Copy current section states to inputs for logging
    for (int i = 0; i < 8; i++) {
      inputs.sections[i] = sections[i];
    }
  }

  @Override
  public void setSection(int section, Animation animation, Color color) {
    setSection(section, animation, color, Color.kBlack);
  }

  @Override
  public void setSection(int section, Animation animation, Color color, Color secondColor) {
    if (section < 0 || section >= 8) {
      System.err.println("Invalid section number: " + section + ". Must be 0-7.");
      return;
    }

    if (color == null) color = Color.kBlack;
    if (secondColor == null) secondColor = Color.kBlack;

    sections[section].animation = animation;
    sections[section].primaryColor = color;
    sections[section].secondaryColor = secondColor;

    // Only update if state changed
    if (animation != previousAnimations[section] ||
        !color.equals(previousPrimaryColors[section]) ||
        !secondColor.equals(previousSecondaryColors[section])) {

      applySection(section);

      previousAnimations[section] = animation;
      previousPrimaryColors[section] = color;
      previousSecondaryColors[section] = secondColor;
    }
  }

  @Override
  public void setAll(Animation animation, Color color) {
    for (int i = 0; i < 8; i++) {
      setSection(i, animation, color);
    }
  }

  @Override
  public void clearAll() {
    // Clear all animation slots
    for (int i = 0; i < 8; i++) {
      candle.setControl(new EmptyAnimation(i));
      sections[i].animation = Animation.OFF;
      sections[i].primaryColor = Color.kBlack;
      previousAnimations[i] = Animation.OFF;
      previousPrimaryColors[i] = Color.kBlack;
    }

    // Set all LEDs to black
    SolidColor black = new SolidColor(0, totalLedCount);
    black.withColor(PhoenixUtils.toCTREColor(Color.kBlack));
    candle.setControl(black);
  }

  /**
   * Apply animation to a specific section.
   * Uses the section number as the animation slot (0-7).
   */
  private void applySection(int sectionIndex) {
    LedSection section = sections[sectionIndex];
    int start = section.startIndex;
    int end = section.endIndex;
    Color primary = section.primaryColor;
    Color secondary = section.secondaryColor;
    int slot = sectionIndex; // Each section uses its own slot

    switch (section.animation) {
      case OFF -> {
        // Clear animation and set to black
        candle.setControl(new EmptyAnimation(slot));
        SolidColor black = new SolidColor(start, end);
        black.withColor(PhoenixUtils.toCTREColor(Color.kBlack));
        candle.setControl(black);
      }

      case SOLID -> {
        // Clear any running animation and set solid color
        candle.setControl(new EmptyAnimation(slot));
        SolidColor solid = new SolidColor(start, end);
        solid.withColor(PhoenixUtils.toCTREColor(primary));
        candle.setControl(solid);
      }

      case COLOR_FLOW -> {
        ColorFlowAnimation anim = new ColorFlowAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed)
            .withDirection(AnimationDirectionValue.Forward);
        candle.setControl(anim);
      }

      case FIRE -> {
        FireAnimation anim = new FireAnimation(start, end)
            .withSlot(slot)
            .withFrameRate(section.speed)
            .withBrightness(section.brightness)
            .withSparking(0.5)
            .withCooling(0.3)
            .withDirection(AnimationDirectionValue.Forward);
        candle.setControl(anim);
      }

      case LARSON -> {
        LarsonAnimation anim = new LarsonAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed)
            .withBounceMode(LarsonBounceValue.Center)
            .withSize(7);
        candle.setControl(anim);
      }

      case RAINBOW -> {
        RainbowAnimation anim = new RainbowAnimation(start, end)
            .withSlot(slot)
            .withFrameRate(section.speed)
            .withBrightness(section.brightness)
            .withDirection(AnimationDirectionValue.Forward);
        candle.setControl(anim);
      }

      case RGB_FADE -> {
        RgbFadeAnimation anim = new RgbFadeAnimation(start, end)
            .withSlot(slot)
            .withBrightness(section.brightness)
            .withFrameRate(section.speed);
        candle.setControl(anim);
      }

      case SINGLE_FADE -> {
        SingleFadeAnimation anim = new SingleFadeAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed);
        candle.setControl(anim);
      }

      case STROBE -> {
        StrobeAnimation anim = new StrobeAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed);
        candle.setControl(anim);
      }

      case TWINKLE -> {
        TwinkleAnimation anim = new TwinkleAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed)
            .withMaxLEDsOnProportion(0.5);
        candle.setControl(anim);
      }

      case TWINKLE_OFF -> {
        TwinkleOffAnimation anim = new TwinkleOffAnimation(start, end)
            .withSlot(slot)
            .withColor(PhoenixUtils.toCTREColor(primary))
            .withFrameRate(section.speed)
            .withMaxLEDsOnProportion(0.5);
        candle.setControl(anim);
      }
    }
  }

  /**
   * Set animation speed for a section (0.0 to 1.0, default 1.0)
   */
  public void setSectionSpeed(int section, double speed) {
    if (section >= 0 && section < 8) {
      sections[section].speed = Math.max(0.0, Math.min(1.0, speed));
      applySection(section);
    }
  }

  /**
   * Set animation brightness for a section (0.0 to 1.0, default 1.0)
   */
  public void setSectionBrightness(int section, double brightness) {
    if (section >= 0 && section < 8) {
      sections[section].brightness = Math.max(0.0, Math.min(1.0, brightness));
      applySection(section);
    }
  }

  /**
   * Get the LED range for a specific section
   *
   * @param section Section number (0-7)
   * @return Array with [startIndex, endIndex]
   */
  public int[] getSectionRange(int section) {
    if (section >= 0 && section < 8) {
      return new int[] {sections[section].startIndex, sections[section].endIndex};
    }
    return new int[] {0, 0};
  }
}
