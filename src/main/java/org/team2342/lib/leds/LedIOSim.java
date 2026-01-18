// Copyright (c) 2026 Team 2342
// https://github.com/FRCTeamPhoenix
//
// This source code is licensed under the MIT License.
// See the LICENSE file in the root directory of this project.

package org.team2342.lib.leds;

import edu.wpi.first.wpilibj.util.Color;

/**
 * Simulation implementation of LED IO.
 * Tracks LED section states for testing and logging purposes.
 */
public class LedIOSim implements LedIO {
  private final LedSection[] sections = new LedSection[8];

  public LedIOSim() {
    // Initialize all sections
    for (int i = 0; i < 8; i++) {
      sections[i] = new LedSection();
      sections[i].startIndex = i * 10; // Simulate 10 LEDs per section
      sections[i].endIndex = (i + 1) * 10;
      sections[i].animation = Animation.OFF;
      sections[i].primaryColor = Color.kBlack;
      sections[i].secondaryColor = Color.kBlack;
    }
  }

  @Override
  public void updateInputs(LedIOInputs inputs) {
    // Copy section states to inputs for logging
    for (int i = 0; i < 8; i++) {
      inputs.sections[i] = sections[i];
    }

    // Can add custom logging here if needed
    // Logger.recordOutput("LED/Sections", sections);
  }

  @Override
  public void setSection(int section, Animation animation, Color color) {
    setSection(section, animation, color, Color.kBlack);
  }

  @Override
  public void setSection(int section, Animation animation, Color color, Color secondColor) {
    if (section >= 0 && section < 8) {
      sections[section].animation = animation;
      sections[section].primaryColor = color != null ? color : Color.kBlack;
      sections[section].secondaryColor = secondColor != null ? secondColor : Color.kBlack;
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
    for (int i = 0; i < 8; i++) {
      sections[i].animation = Animation.OFF;
      sections[i].primaryColor = Color.kBlack;
      sections[i].secondaryColor = Color.kBlack;
    }
  }
}
