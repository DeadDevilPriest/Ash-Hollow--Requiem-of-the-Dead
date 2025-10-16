package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.*;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import Ash_Hollow_Requiem.renderer.MerchantGuiRenderer;
import Ash_Hollow_Requiem.renderer.MerchantGuiRenderer.MerchantRotation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BountyBoardScreen extends Screen {
    private static final ResourceLocation BOARD_TEXTURE = new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/gui/bounty_board.png");

    // Base dimensions for GUI Scale 3 (largest)
    private static final int BASE_BOARD_WIDTH = 300;
    private static final int BASE_BOARD_HEIGHT = 200;
    private static final int ENTITY_CLICK_RADIUS = 50;

    // ✅ Bounty categories (keeping this in BountyBoardScreen for UI)
    public enum BountyCategory {
        BOUNTY("Bounty", ChatFormatting.GREEN),
        SPECIAL("Special", ChatFormatting.BLUE),
        HORDE("Horde", ChatFormatting.YELLOW),
        ELITE("Elite", ChatFormatting.GOLD),
        BOSS("Boss", ChatFormatting.DARK_RED),
        EVENT("Event", ChatFormatting.LIGHT_PURPLE);

        private final String displayName;
        private final ChatFormatting color;

        BountyCategory(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    // Store 8 bounties per category (received from server)
    private Map<BountyCategory, List<Bounty>> categoryBounties = new HashMap<>();
    private boolean bountiesLoaded = false;

    // Merchant rendering data
    private MerchantRotation tokenMerchantRotation;
    private MerchantRotation lootMerchantRotation;

    // Dynamic positions calculated based on GUI scale
    private int scaledBoardWidth;
    private int scaledBoardHeight;
    private int boardX;
    private int boardY;

    private int tokenMerchantX;
    private int tokenMerchantY;
    private int lootMerchantX;
    private int lootMerchantY;

    // Category buttons
    private Button[] categoryButtons = new Button[BountyCategory.values().length];

    public BountyBoardScreen(int coins, int tokens) {
        super(Component.literal("Bounty Board"));
        this.tokenMerchantRotation = new MerchantRotation();
        this.lootMerchantRotation = new MerchantRotation();

        // Initialize empty bounty lists (will be loaded from server)
        for (BountyCategory category : BountyCategory.values()) {
            categoryBounties.put(category, new ArrayList<>());
        }
    }

    /**
     * Called by packet handler when bounties are received from server
     */
    public void setBountiesForCategory(BountyCategory category, List<Bounty> bounties) {
        categoryBounties.put(category, bounties);
        bountiesLoaded = true;
    }

    @Override
    protected void init() {
        super.init();

        calculateScaledDimensions();

        boardX = (this.width - scaledBoardWidth) / 2;
        boardY = (this.height - scaledBoardHeight) / 2;

        // Position merchants OUTSIDE the board (left and right)
        int merchantSize = (int)(scaledBoardWidth * 0.15f);
        tokenMerchantX = boardX - merchantSize - 20;
        tokenMerchantY = boardY + scaledBoardHeight / 2;
        lootMerchantX = boardX + scaledBoardWidth + merchantSize + 20;
        lootMerchantY = boardY + scaledBoardHeight / 2;

        // Create 6 category buttons (top row)
        BountyCategory[] categories = BountyCategory.values();

        int buttonWidth = (int)(scaledBoardWidth * 0.14f);
        int buttonHeight = (int)(scaledBoardHeight * 0.15f);
        int buttonStartY = boardY + (int)(scaledBoardHeight * 0.12f);
        int buttonSpacing = (int)(scaledBoardWidth * 0.015f);
        int totalButtonWidth = (buttonWidth * 6) + (buttonSpacing * 5);
        int buttonStartX = boardX + (scaledBoardWidth - totalButtonWidth) / 2;

        for (int i = 0; i < categories.length; i++) {
            int x = buttonStartX + (i * (buttonWidth + buttonSpacing));
            final BountyCategory category = categories[i];

            categoryButtons[i] = Button.builder(
                    Component.literal(category.getDisplayName()).withStyle(category.getColor(), ChatFormatting.BOLD),
                    btn -> openBountyViewer(category)
            ).bounds(x, buttonStartY, buttonWidth, buttonHeight).build();

            this.addRenderableWidget(categoryButtons[i]);
        }

        // My Active Hunts button
        int bottomButtonY = boardY + scaledBoardHeight - (int)(scaledBoardHeight * 0.12f);
        this.addRenderableWidget(Button.builder(
                Component.literal("My Active Hunts"),
                btn -> openMyHunts()
        ).bounds(boardX + scaledBoardWidth / 2 - 60, bottomButtonY, 120, 20).build());

        // Close button
        this.addRenderableWidget(Button.builder(
                Component.literal("Close Board"),
                btn -> this.onClose()
        ).bounds(boardX + scaledBoardWidth / 2 - 50, bottomButtonY + 25, 100, 20).build());

        // ✅ Request bounties from server for all categories
        requestBountiesFromServer();
    }

    /**
     * Request persistent bounties from server
     */
    private void requestBountiesFromServer() {
        for (BountyCategory category : BountyCategory.values()) {
            // TODO: Send packet to server requesting bounties for this category
            // PacketHandler.sendToServer(new RequestBountiesPacket(category));

            // TEMPORARY: Generate client-side until packet is implemented
            loadTemporaryBounties(category);
        }
    }

    /**
     * TEMPORARY: Load bounties client-side (remove when server sync is ready)
     */
    private void loadTemporaryBounties(BountyCategory category) {
        List<Bounty> bounties = new ArrayList<>();
        long currentTime = Minecraft.getInstance().player.level().getGameTime();

        for (int i = 0; i < 8; i++) {
            BountyRarity rarity = getRandomRarityForCategory(category);

            // Generate based on category type
            Bounty bounty = switch(category) {
                case BOUNTY -> BountyGenerator.generateStandardBounty(rarity, currentTime);
                case SPECIAL -> BountyGenerator.generateStandardBounty(rarity, currentTime);
                case HORDE -> BountyGenerator.generateHordeBounty(rarity, currentTime);
                case ELITE -> BountyGenerator.generateEliteBounty(rarity, currentTime);
                case BOSS -> {
                    // ✅ BOSS category: Epic+ with TOKEN COST
                    BountyRarity bossRarity = getRandomBossRarity();
                    yield BountyGenerator.generateBossBounty(bossRarity, currentTime);
                }
                case EVENT -> BountyGenerator.generateStandardBounty(rarity, currentTime);
            };

            bounties.add(bounty);
        }

        categoryBounties.put(category, bounties);
        bountiesLoaded = true;
    }

    private BountyRarity getRandomRarityForCategory(BountyCategory category) {
        int roll = new java.util.Random().nextInt(100);

        return switch (category) {
            case BOSS, EVENT -> {
                if (roll < 30) yield BountyRarity.EPIC;
                if (roll < 70) yield BountyRarity.LEGENDARY;
                yield BountyRarity.EPIC;
            }
            case ELITE -> {
                if (roll < 40) yield BountyRarity.RARE;
                if (roll < 80) yield BountyRarity.EPIC;
                yield BountyRarity.LEGENDARY;
            }
            default -> {
                if (roll < 40) yield BountyRarity.COMMON;
                if (roll < 70) yield BountyRarity.UNCOMMON;
                if (roll < 90) yield BountyRarity.RARE;
                yield BountyRarity.EPIC;
            }
        };
    }

    /**
     * Get random boss rarity (Epic to Unknown)
     */
    private BountyRarity getRandomBossRarity() {
        java.util.Random random = new java.util.Random();
        int roll = random.nextInt(100);

        if (roll < 30) return BountyRarity.EPIC;
        if (roll < 55) return BountyRarity.LEGENDARY;
        if (roll < 75) return BountyRarity.ANCIENT;
        if (roll < 87) return BountyRarity.CURSED;
        if (roll < 93) return BountyRarity.EXPERIMENTAL;
        if (roll < 96) return BountyRarity.HOLLOW;
        if (roll < 98) return BountyRarity.GODLY;
        if (roll < 99) return BountyRarity.INSANE;
        return BountyRarity.UNKNOWN;
    }

    private void calculateScaledDimensions() {
        Minecraft mc = Minecraft.getInstance();
        int guiScale = mc.options.guiScale().get();

        if (guiScale == 0) {
            guiScale = 5;
        }

        float scaleFactor = Math.max(1.0f, 5.0f / guiScale);
        scaledBoardWidth = (int)(BASE_BOARD_WIDTH * scaleFactor);
        scaledBoardHeight = (int)(BASE_BOARD_HEIGHT * scaleFactor);

        scaledBoardWidth = Math.max(scaledBoardWidth, 280);
        scaledBoardHeight = Math.max(scaledBoardHeight, 200);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isPointInRadius(mouseX, mouseY, tokenMerchantX, tokenMerchantY, ENTITY_CLICK_RADIUS)) {
            openTokenMerchant();
            return true;
        }

        if (isPointInRadius(mouseX, mouseY, lootMerchantX, lootMerchantY, ENTITY_CLICK_RADIUS)) {
            openLootShop();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isPointInRadius(double pointX, double pointY, int targetX, int targetY, int radius) {
        double dx = pointX - targetX;
        double dy = pointY - targetY;
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        tokenMerchantRotation.updateFromMouse(mouseX, mouseY, tokenMerchantX, tokenMerchantY);
        lootMerchantRotation.updateFromMouse(mouseX, mouseY, lootMerchantX, lootMerchantY);

        graphics.fill(boardX, boardY, boardX + scaledBoardWidth, boardY + scaledBoardHeight, 0xFF2C1810);
        graphics.fill(boardX + 4, boardY + 4, boardX + scaledBoardWidth - 4, boardY + scaledBoardHeight - 4, 0xFF8B6F47);

        int innerMargin = (int)(scaledBoardWidth * 0.08f);
        int innerTop = boardY + (int)(scaledBoardHeight * 0.32f);
        graphics.fill(
                boardX + innerMargin,
                innerTop,
                boardX + scaledBoardWidth - innerMargin,
                boardY + scaledBoardHeight - innerMargin,
                0xFFC9A677
        );

        graphics.drawCenteredString(this.font, "ASH HOLLOW BOUNTY BOARD",
                this.width / 2, boardY + 15, 0xFFFFFF);

        graphics.drawCenteredString(this.font, "Select Bounty Category",
                this.width / 2, boardY + 30, 0xCCCCCC);

        // GET LIVE PLAYER DATA
        int playerCoins = 0;
        int playerTokens = 0;
        if (minecraft.player != null) {
            playerCoins = PlayerDataAPI.getCoins(minecraft.player);
            playerTokens = PlayerDataAPI.getTokens(minecraft.player);
        }

        // Currency display boxes
        int currencyBoxWidth = 100;
        int currencyBoxHeight = 25;

        graphics.fill(
                boardX + innerMargin + 10,
                boardY + scaledBoardHeight - innerMargin - currencyBoxHeight - 60,
                boardX + innerMargin + 10 + currencyBoxWidth,
                boardY + scaledBoardHeight - innerMargin - 60,
                0xFF5C4033
        );
        graphics.drawString(this.font, "Coins: " + playerCoins,
                boardX + innerMargin + 15,
                boardY + scaledBoardHeight - innerMargin - currencyBoxHeight - 52,
                0xFFD700);

        graphics.fill(
                boardX + scaledBoardWidth - innerMargin - currencyBoxWidth - 10,
                boardY + scaledBoardHeight - innerMargin - currencyBoxHeight - 60,
                boardX + scaledBoardWidth - innerMargin - 10,
                boardY + scaledBoardHeight - innerMargin - 60,
                0xFF5C4033
        );
        graphics.drawString(this.font, "Tokens: " + playerTokens,
                boardX + scaledBoardWidth - innerMargin - currencyBoxWidth - 5,
                boardY + scaledBoardHeight - innerMargin - currencyBoxHeight - 52,
                0xFF5AFF5A);

        int merchantScale = (int)(25 * (scaledBoardWidth / (float)BASE_BOARD_WIDTH));
        MerchantGuiRenderer.renderMerchant(graphics, tokenMerchantX, tokenMerchantY, merchantScale, tokenMerchantRotation, true);
        MerchantGuiRenderer.renderMerchant(graphics, lootMerchantX, lootMerchantY, merchantScale, lootMerchantRotation, false);

        if (isPointInRadius(mouseX, mouseY, tokenMerchantX, tokenMerchantY, ENTITY_CLICK_RADIUS)) {
            graphics.drawCenteredString(this.font, "Token Merchant",
                    tokenMerchantX, tokenMerchantY - 60, 0xFFFFFF);
        }

        if (isPointInRadius(mouseX, mouseY, lootMerchantX, lootMerchantY, ENTITY_CLICK_RADIUS)) {
            graphics.drawCenteredString(this.font, "Loot Shop",
                    lootMerchantX, lootMerchantY - 60, 0xFFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void openBountyViewer(BountyCategory category) {
        List<Bounty> bounties = categoryBounties.get(category);
        int coins = minecraft.player != null ? PlayerDataAPI.getCoins(minecraft.player) : 0;
        int tokens = minecraft.player != null ? PlayerDataAPI.getTokens(minecraft.player) : 0;

        minecraft.setScreen(new BountyViewerScreen(this, category, bounties, coins, tokens));
    }

    private void openTokenMerchant() {
        int coins = minecraft.player != null ? PlayerDataAPI.getCoins(minecraft.player) : 0;
        int tokens = minecraft.player != null ? PlayerDataAPI.getTokens(minecraft.player) : 0;
        minecraft.setScreen(new TokenMerchantScreen(this, coins, tokens));
    }

    private void openLootShop() {
        int coins = minecraft.player != null ? PlayerDataAPI.getCoins(minecraft.player) : 0;
        minecraft.setScreen(new LootShopScreen(this, coins));
    }

    private void openMyHunts() {
        if (minecraft.player != null) {
            List<Bounty> activeBounties = BountyManager.getPlayerBounties(
                    minecraft.player.getUUID()
            );
            minecraft.setScreen(new ActiveHuntsScreen(this, activeBounties));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}