package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.playerdata.PlayerData;
import Ash_Hollow_Requiem.playerdata.PlayerDataProvider;
import Ash_Hollow_Requiem.skilltributes.Attributes;
import Ash_Hollow_Requiem.skilltributes.Skills;
import Ash_Hollow_Requiem.skilltributes.SkillRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillInterface extends Screen {
    private final Player player;
    private PlayerData playerData;
    private Attributes attributes;
    private Map<String, Integer> skillLevels;
    private int skillPoints;

    // UI Layout
    private static final int LEFT_MARGIN = 20;
    private static final int ATTRIBUTE_SECTION_WIDTH = 200;
    private static final int SKILL_SECTION_X = 240;

    private List<SkillButton> skillButtons = new ArrayList<>();

    public SkillInterface(Component title, Player player) {
        super(title != null ? title : Component.literal("Alchemist's Enhancement Tool"));
        this.player = player;

        // Retrieve player data from capability
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            this.playerData = data;
            this.attributes = data.getAttributes();
            this.skillLevels = data.getSkillLevels();
            this.skillPoints = data.getSkillPoints();
        });
    }

    @Override
    protected void init() {
        super.init();

        // Create skill buttons
        skillButtons.clear();
        int yPosition = 60;

        for (Skills.Skill skill : Skills.getAllSkills()) {
            int currentLevel = skillLevels.getOrDefault(skill.getId(), 0);

            // Check if can upgrade
            boolean canUpgrade = SkillRegistry.canUpgradeSkill(
                    skill.getId(),
                    attributes,
                    skillLevels,
                    skillPoints
            );

            SkillButton button = new SkillButton(
                    SKILL_SECTION_X,
                    yPosition,
                    200,
                    40,
                    skill,
                    currentLevel,
                    canUpgrade,
                    btn -> upgradeSkill(skill.getId())
            );

            this.addRenderableWidget(button);
            skillButtons.add(button);
            yPosition += 45;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        // Title
        graphics.drawCenteredString(
                this.font,
                "Alchemist's Enhancement Tool",
                this.width / 2,
                10,
                0xAA00FF
        );

        // Skill Points Display
        String pointsText = "Skill Points: " + skillPoints;
        graphics.drawString(
                this.font,
                pointsText,
                this.width - 120,
                10,
                skillPoints > 0 ? 0x00FF00 : 0xFFFFFF
        );

        // === ATTRIBUTES SECTION === //
        renderAttributesSection(graphics);

        // === SKILLS SECTION === //
        graphics.drawString(
                this.font,
                "Available Skills",
                SKILL_SECTION_X,
                40,
                0xFFFF00
        );

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderAttributesSection(GuiGraphics graphics) {
        int y = 40;

        graphics.drawString(this.font, "Attributes", LEFT_MARGIN, y, 0xFFFF00);
        y += 15;

        // Render each attribute with progress bar
        renderAttribute(graphics, "Strength", attributes.getStrength(),
                attributes.getStrengthXP(),
                attributes.getXPRequiredForNextLevel(Attributes.AttributeType.STRENGTH),
                y, 0xFF4444);
        y += 30;

        renderAttribute(graphics, "Constitution", attributes.getConstitution(),
                attributes.getConstitutionXP(),
                attributes.getXPRequiredForNextLevel(Attributes.AttributeType.CONSTITUTION),
                y, 0x44FF44);
        y += 30;

        renderAttribute(graphics, "Dexterity", attributes.getDexterity(),
                attributes.getDexterityXP(),
                attributes.getXPRequiredForNextLevel(Attributes.AttributeType.DEXTERITY),
                y, 0x4444FF);
        y += 30;

        renderAttribute(graphics, "Willpower", attributes.getWillpower(),
                attributes.getWillpowerXP(),
                attributes.getXPRequiredForNextLevel(Attributes.AttributeType.WILLPOWER),
                y, 0xFFFF44);
        y += 30;

        renderAttribute(graphics, "Intelligence", attributes.getIntelligence(),
                attributes.getIntelligenceXP(),
                attributes.getXPRequiredForNextLevel(Attributes.AttributeType.INTELLIGENCE),
                y, 0xFF44FF);
    }

    private void renderAttribute(GuiGraphics graphics, String name, int level,
                                 float currentXP, float requiredXP, int y, int color) {
        // Attribute name and level
        String text = name + ": " + level;
        graphics.drawString(this.font, text, LEFT_MARGIN, y, 0xFFFFFF);

        // Progress bar
        int barWidth = 150;
        int barHeight = 8;
        int barX = LEFT_MARGIN;
        int barY = y + 12;

        // Background
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);

        // Progress
        float progress = Math.min(1.0f, currentXP / requiredXP);
        int fillWidth = (int)(barWidth * progress);
        graphics.fill(barX, barY, barX + fillWidth, barY + barHeight, color);

        // XP Text
        String xpText = String.format("%.0f / %.0f XP", currentXP, requiredXP);
        graphics.drawString(this.font, xpText, barX + barWidth + 5, barY, 0xAAAAAA);
    }

    private void upgradeSkill(String skillId) {
        if (playerData != null) {
            Skills.Skill skill = SkillRegistry.getSkill(skillId);
            if (skill == null) return;

            int currentLevel = playerData.getSkillLevel(skillId);
            int cost = skill.getCostForLevel(currentLevel);

            // Attempt upgrade
            if (playerData.spendSkillPoints(cost)) {
                playerData.setSkillLevel(skillId, currentLevel + 1);

                // Refresh UI
                this.skillPoints = playerData.getSkillPoints();
                this.skillLevels = playerData.getSkillLevels();

                // Rebuild buttons
                this.clearWidgets();
                this.init();

                // TODO: Send packet to server to sync the upgrade
                player.displayClientMessage(
                        Component.literal("Upgraded " + skill.getName() + " to level " + (currentLevel + 1))
                                .withStyle(net.minecraft.ChatFormatting.GREEN),
                        false
                );
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // === SKILL BUTTON === //
    private class SkillButton extends Button {
        private final Skills.Skill skill;
        private final int currentLevel;
        private final boolean canUpgrade;

        public SkillButton(int x, int y, int width, int height,
                           Skills.Skill skill, int currentLevel, boolean canUpgrade,
                           OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.skill = skill;
            this.currentLevel = currentLevel;
            this.canUpgrade = canUpgrade;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            // Background
            int bgColor = canUpgrade ? 0x8800FF00 : (currentLevel > 0 ? 0x880000FF : 0x88333333);
            graphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width, this.getY() + this.height,
                    bgColor);

            // Border
            int borderColor = this.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF888888;
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, borderColor);
            graphics.fill(this.getX(), this.getY() + this.height - 1,
                    this.getX() + this.width, this.getY() + this.height, borderColor);

            // Skill name
            String nameText = skill.getName() + " [" + currentLevel + "/" + skill.getMaxLevel() + "]";
            graphics.drawString(font, nameText, this.getX() + 5, this.getY() + 5, 0xFFFFFF);

            // Cost
            if (currentLevel < skill.getMaxLevel()) {
                int cost = skill.getCostForLevel(currentLevel);
                String costText = "Cost: " + cost + " pts";
                int costColor = canUpgrade ? 0x00FF00 : 0xFF0000;
                graphics.drawString(font, costText, this.getX() + 5, this.getY() + 18, costColor);
            } else {
                graphics.drawString(font, "MAX LEVEL", this.getX() + 5, this.getY() + 18, 0xFFFF00);
            }

            // Effect preview
            String effectText = skill.getDescriptionForLevel(Math.max(1, currentLevel));
            if (effectText.length() > 35) {
                effectText = effectText.substring(0, 32) + "...";
            }
            graphics.drawString(font, effectText, this.getX() + 5, this.getY() + 30, 0xAAAAAA);
        }
    }
}