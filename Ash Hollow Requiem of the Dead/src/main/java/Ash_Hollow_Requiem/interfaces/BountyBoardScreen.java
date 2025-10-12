// ========================================
// BountyBoardScreen.java
// ========================================
package Ash_Hollow_Requiem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public class BountyBoardScreen extends Screen {
    private static final ResourceLocation BOARD_TEXTURE = new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/gui/bounty_board.png");
    private static final int BOARD_WIDTH = 256;
    private static final int BOARD_HEIGHT = 240;

    private List<Bounty> normalBounties;
    private List<BossBounty> bossBounties;
    private int playerCoins;
    private int playerBossTokens;

    private Button tokenMerchantButton;
    private Button lootShopButton;
    private Button myHuntsButton;

    public BountyBoardScreen(int coins, int tokens) {
        super(Component.literal("Bounty Board"));
        this.playerCoins = coins;
        this.playerBossTokens = tokens;
        this.normalBounties = new ArrayList<>();
        this.bossBounties = new ArrayList<>();
        loadBounties();
    }

    private void loadBounties() {
        // Load 5 normal bounties
        normalBounties.add(new Bounty("Hollow Bandit", 3, "Ash Hollow Mines", 150, 3));
        normalBounties.add(new Bounty("Cursed Miner", 2, "Deep Caverns", 80, 2));
        normalBounties.add(new Bounty("Spectral Wolf", 4, "Haunted Forest", 200, 5));
        normalBounties.add(new Bounty("Vengeful Spirit", 3, "Abandoned Chapel", 120, 3));
        normalBounties.add(new Bounty("Ash Raider", 2, "Eastern Outpost", 90, 2));

        // Load 3 boss bounties
        bossBounties.add(new BossBounty("The Wailing Revenant", 5, 15, 1000, true));
        bossBounties.add(new BossBounty("Bone Lord of Ash", 4, 10, 750, true));
        bossBounties.add(new BossBounty("Crimson Executioner", 5, 25, 1500, true));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - BOARD_WIDTH) / 2;
        int centerY = (this.height - BOARD_HEIGHT) / 2;

        // Normal bounty buttons (5 bounties)
        int bountyStartY = centerY + 30;
        for (int i = 0; i < normalBounties.size(); i++) {
            Bounty bounty = normalBounties.get(i);
            int row = i / 2;
            int col = i % 2;
            int x = centerX + 10 + (col * 120);
            int y = bountyStartY + (row * 35);

            this.addRenderableWidget(Button.builder(
                    Component.literal(bounty.name),
                    btn -> openBountyDetails(bounty)
            ).bounds(x, y, 110, 30).build());
        }

        // Boss bounty buttons (3 bounties)
        int bossStartY = centerY + 120;
        for (int i = 0; i < bossBounties.size(); i++) {
            BossBounty boss = bossBounties.get(i);
            int x = centerX + 10 + (i * 78);
            int y = bossStartY;

            Button bossBtn = Button.builder(
                    Component.literal("BOSS"),
                    btn -> openBossBountyDetails(boss)
            ).bounds(x, y, 75, 30).build();

            // Disable if not enough tokens
            if (playerBossTokens < boss.tokenCost) {
                bossBtn.active = false;
            }

            this.addRenderableWidget(bossBtn);
        }

        // Token Merchant button (lower left)
        tokenMerchantButton = Button.builder(
                Component.literal("Token\nMerchant"),
                btn -> openTokenMerchant()
        ).bounds(centerX + 10, centerY + BOARD_HEIGHT - 50, 60, 40).build();
        this.addRenderableWidget(tokenMerchantButton);

        // Loot Shop button (lower right)
        lootShopButton = Button.builder(
                Component.literal("Loot\nShop"),
                btn -> openLootShop()
        ).bounds(centerX + BOARD_WIDTH - 70, centerY + BOARD_HEIGHT - 50, 60, 40).build();
        this.addRenderableWidget(lootShopButton);

        // My Hunts button (bottom center)
        myHuntsButton = Button.builder(
                Component.literal("My Active Hunts"),
                btn -> openMyHunts()
        ).bounds(centerX + 75, centerY + BOARD_HEIGHT - 45, 106, 20).build();
        this.addRenderableWidget(myHuntsButton);

        // Close button
        this.addRenderableWidget(Button.builder(
                Component.literal("Close Board"),
                btn -> this.onClose()
        ).bounds(centerX + 75, centerY + BOARD_HEIGHT - 20, 106, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = (this.width - BOARD_WIDTH) / 2;
        int centerY = (this.height - BOARD_HEIGHT) / 2;

        // Draw board background
        graphics.fill(centerX, centerY, centerX + BOARD_WIDTH, centerY + BOARD_HEIGHT, 0xFF8B6F47);
        graphics.fill(centerX + 4, centerY + 4, centerX + BOARD_WIDTH - 4, centerY + BOARD_HEIGHT - 4, 0xFFC9A677);

        // Title
        graphics.drawCenteredString(this.font, "ASH HOLLOW BOUNTY BOARD",
                this.width / 2, centerY + 10, 0x3A1A0A);

        // Section labels
        graphics.drawString(this.font, "Standard Contracts", centerX + 10, centerY + 20, 0x3A1A0A);
        graphics.drawString(this.font, "Legendary Contracts", centerX + 10, centerY + 110, 0x8B1A1A);

        // Player balance
        graphics.drawString(this.font, "Coins: " + playerCoins, centerX + 10, centerY + 160, 0xFFD700);
        graphics.drawString(this.font, "Tokens: " + playerBossTokens, centerX + 10, centerY + 170, 0xFF5AFF5A);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void openBountyDetails(Bounty bounty) {
        minecraft.setScreen(new BountyDetailsScreen(this, bounty, playerCoins, playerBossTokens));
    }

    private void openBossBountyDetails(BossBounty boss) {
        minecraft.setScreen(new BossBountyDetailsScreen(this, boss, playerCoins, playerBossTokens));
    }

    private void openTokenMerchant() {
        minecraft.setScreen(new TokenMerchantScreen(this, playerCoins, playerBossTokens));
    }

    private void openLootShop() {
        minecraft.setScreen(new LootShopScreen(this, playerCoins));
    }

    private void openMyHunts() {
        minecraft.setScreen(new ActiveHuntsScreen(this));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}

// ========================================
// Bounty.java - Data class for normal bounties
// ========================================
class Bounty {
    public String name;
    public int threatLevel; // 1-5 stars
    public String location;
    public int coinReward;
    public int tokenReward;
    public String description;
    public List<String> objectives;

    public Bounty(String name, int threatLevel, String location, int coinReward, int tokenReward) {
        this.name = name;
        this.threatLevel = threatLevel;
        this.location = location;
        this.coinReward = coinReward;
        this.tokenReward = tokenReward;
        this.description = "A dangerous enemy terrorizing " + location + ".";
        this.objectives = new ArrayList<>();
        this.objectives.add("Eliminate Target (0/1)");
        this.objectives.add("Collect Evidence (0/1)");
        this.objectives.add("Return within 3 days");
    }

    public String getStars() {
        return "*".repeat(threatLevel);
    }
}

// ========================================
// BossBounty.java - Data class for boss bounties
// ========================================
class BossBounty {
    public String name;
    public int threatLevel; // 1-5 stars
    public int tokenCost;
    public int coinReward;
    public boolean isLocked;
    public String description;
    public List<String> mechanics;

    public BossBounty(String name, int threatLevel, int tokenCost, int coinReward, boolean isLocked) {
        this.name = name;
        this.threatLevel = threatLevel;
        this.tokenCost = tokenCost;
        this.coinReward = coinReward;
        this.isLocked = isLocked;
        this.description = "An ancient legendary threat. Extreme danger.";
        this.mechanics = new ArrayList<>();
        this.mechanics.add("Phase 1: Summons minions");
        this.mechanics.add("Phase 2: Area damage");
        this.mechanics.add("Phase 3: Enrage mode");
    }

    public String getStars() {
        return "*".repeat(threatLevel);
    }
}

// ========================================
// BountyDetailsScreen.java
// ========================================
class BountyDetailsScreen extends Screen {
    private final Screen parent;
    private final Bounty bounty;
    private final int playerCoins;
    private final int playerTokens;

    protected BountyDetailsScreen(Screen parent, Bounty bounty, int coins, int tokens) {
        super(Component.literal("Bounty Details"));
        this.parent = parent;
        this.bounty = bounty;
        this.playerCoins = coins;
        this.playerTokens = tokens;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Accept button
        this.addRenderableWidget(Button.builder(
                Component.literal("Accept Contract"),
                btn -> acceptBounty()
        ).bounds(centerX - 110, centerY + 80, 100, 20).build());

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX + 10, centerY + 80, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 80;

        // Poster background
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 200, 0xFF8B6F47);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 196, 0xFFC9A677);

        // Title
        graphics.drawCenteredString(this.font, "WANTED", centerX, centerY + 10, 0x8B1A1A);
        graphics.drawCenteredString(this.font, "DEAD OR ALIVE", centerX, centerY + 20, 0x3A1A0A);

        // Portrait placeholder
        graphics.fill(centerX - 40, centerY + 35, centerX + 40, centerY + 115, 0xFF2A2A2A);

        // Details
        int yOffset = centerY + 125;
        graphics.drawCenteredString(this.font, "Target: " + bounty.name, centerX, yOffset, 0x3A1A0A);
        graphics.drawCenteredString(this.font, "Threat: " + bounty.getStars(), centerX, yOffset + 10, 0xFFD700);
        graphics.drawCenteredString(this.font, "Location: " + bounty.location, centerX, yOffset + 20, 0x3A1A0A);

        // Rewards
        graphics.drawCenteredString(this.font, "Rewards:", centerX, yOffset + 35, 0x8B1A1A);
        graphics.drawCenteredString(this.font, bounty.coinReward + " Coins + " + bounty.tokenReward + " Tokens",
                centerX, yOffset + 45, 0x3A1A0A);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void acceptBounty() {
        // TODO: Send packet to server to accept bounty
        minecraft.player.sendSystemMessage(Component.literal("Bounty Accepted: " + bounty.name));
        minecraft.setScreen(parent);
    }
}

// ========================================
// BossBountyDetailsScreen.java
// ========================================
class BossBountyDetailsScreen extends Screen {
    private final Screen parent;
    private final BossBounty boss;
    private final int playerCoins;
    private final int playerTokens;

    protected BossBountyDetailsScreen(Screen parent, BossBounty boss, int coins, int tokens) {
        super(Component.literal("Boss Bounty Details"));
        this.parent = parent;
        this.boss = boss;
        this.playerCoins = coins;
        this.playerTokens = tokens;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        boolean canAfford = playerTokens >= boss.tokenCost;

        // Purchase button
        Button purchaseBtn = Button.builder(
                Component.literal("Purchase (" + boss.tokenCost + " Tokens)"),
                btn -> purchaseBounty()
        ).bounds(centerX - 110, centerY + 80, 100, 20).build();
        purchaseBtn.active = canAfford;
        this.addRenderableWidget(purchaseBtn);

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX + 10, centerY + 80, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 80;

        // Legendary poster background
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 200, 0xFF4A1A4A);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 196, 0xFF8B1AAA);

        // Title
        graphics.drawCenteredString(this.font, "LEGENDARY BOUNTY", centerX, centerY + 10, 0xFFD700);
        graphics.drawCenteredString(this.font, boss.name, centerX, centerY + 22, 0xFFFFFF);

        // Portrait placeholder (animated skull)
        graphics.fill(centerX - 40, centerY + 35, centerX + 40, centerY + 115, 0xFF1A1A1A);

        // Details
        int yOffset = centerY + 125;
        graphics.drawCenteredString(this.font, "Threat: " + boss.getStars() + " EXTREME", centerX, yOffset, 0xFFFF5A5A);
        graphics.drawCenteredString(this.font, "Cost: " + boss.tokenCost + " Boss Tokens", centerX, yOffset + 10, 0xFF5AFF5A);

        // Lock status
        if (playerTokens < boss.tokenCost) {
            graphics.drawCenteredString(this.font, "LOCKED - Need " + (boss.tokenCost - playerTokens) + " more tokens",
                    centerX, yOffset + 25, 0xFFFF5A5A);
        }

        // Rewards
        graphics.drawCenteredString(this.font, "Rewards:", centerX, yOffset + 40, 0xFFD700);
        graphics.drawCenteredString(this.font, boss.coinReward + " Coins + Legendary Loot",
                centerX, yOffset + 50, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void purchaseBounty() {
        if (playerTokens >= boss.tokenCost) {
            // TODO: Send packet to server to purchase and accept boss bounty
            minecraft.player.sendSystemMessage(Component.literal("Boss Bounty Purchased: " + boss.name));
            minecraft.setScreen(parent);
        }
    }
}

// ========================================
// TokenMerchantScreen.java
// ========================================
class TokenMerchantScreen extends Screen {
    private final Screen parent;
    private int playerCoins;
    private int playerTokens;

    protected TokenMerchantScreen(Screen parent, int coins, int tokens) {
        super(Component.literal("Token Merchant"));
        this.parent = parent;
        this.playerCoins = coins;
        this.playerTokens = tokens;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4 + 40;

        // Exchange buttons
        this.addRenderableWidget(Button.builder(
                Component.literal("100 Coins -> 1 Token"),
                btn -> exchange(100, 1)
        ).bounds(centerX - 100, startY, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("500 Coins -> 6 Tokens (BONUS!)"),
                btn -> exchange(500, 6)
        ).bounds(centerX - 100, startY + 25, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("1000 Coins -> 15 Tokens (BONUS!)"),
                btn -> exchange(1000, 15)
        ).bounds(centerX - 100, startY + 50, 200, 20).build());

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, startY + 90, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(this.font, "Token Merchant", this.width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Coins: " + playerCoins, this.width / 2, 40, 0xFFD700);
        graphics.drawCenteredString(this.font, "Boss Tokens: " + playerTokens, this.width / 2, 50, 0xFF5AFF5A);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void exchange(int coinCost, int tokensGained) {
        if (playerCoins >= coinCost) {
            // TODO: Send packet to server to exchange
            playerCoins -= coinCost;
            playerTokens += tokensGained;
            minecraft.player.sendSystemMessage(Component.literal("Exchanged " + coinCost + " coins for " + tokensGained + " tokens!"));
        } else {
            minecraft.player.sendSystemMessage(Component.literal("Not enough coins!"));
        }
    }
}

// ========================================
// LootShopScreen.java
// ========================================
class LootShopScreen extends Screen {
    private final Screen parent;
    private int playerCoins;

    protected LootShopScreen(Screen parent, int coins) {
        super(Component.literal("Loot Shop"));
        this.parent = parent;
        this.playerCoins = coins;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4 + 40;

        // Sample items
        this.addRenderableWidget(Button.builder(
                Component.literal("Iron Harpoon - 75 Coins"),
                btn -> buyItem("Iron Harpoon", 75)
        ).bounds(centerX - 100, startY, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Steel Harpoon - 200 Coins"),
                btn -> buyItem("Steel Harpoon", 200)
        ).bounds(centerX - 100, startY + 25, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Health Potion - 10 Coins"),
                btn -> buyItem("Health Potion", 10)
        ).bounds(centerX - 100, startY + 50, 200, 20).build());

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, startY + 90, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(this.font, "Loot Shop", this.width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Your Coins: " + playerCoins, this.width / 2, 40, 0xFFD700);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void buyItem(String itemName, int cost) {
        if (playerCoins >= cost) {
            // TODO: Send packet to server to purchase item
            playerCoins -= cost;
            minecraft.player.sendSystemMessage(Component.literal("Purchased " + itemName + "!"));
        } else {
            minecraft.player.sendSystemMessage(Component.literal("Not enough coins!"));
        }
    }
}

// ========================================
// ActiveHuntsScreen.java
// ========================================
class ActiveHuntsScreen extends Screen {
    private final Screen parent;

    protected ActiveHuntsScreen(Screen parent) {
        super(Component.literal("Active Hunts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, this.height - 40, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(this.font, "My Active Hunts", this.width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "No active bounties", this.width / 2, 60, 0xAAAAAA);
        graphics.drawCenteredString(this.font, "(This will show your accepted contracts)", this.width / 2, 75, 0x888888);

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}