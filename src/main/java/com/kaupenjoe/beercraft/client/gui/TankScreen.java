package com.kaupenjoe.beercraft.client.gui;

import com.kaupenjoe.beercraft.BeerCraft;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;

public class TankScreen extends ContainerScreen<TankContainer>
{
    private final ResourceLocation GUI = new ResourceLocation(BeerCraft.MOD_ID,
            "textures/gui/test_tank_gui.png");

    private static final ResourceLocation RECIPE_BUTTON_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/recipe_button.png");

    @Override
    public void init()
    {
        super.init();

        this.addButton(new ImageButton
                (this.guiLeft + 13, this.height + 9, 20, 20,
                        0, 0, 19, RECIPE_BUTTON_TEXTURE,
                        (button) -> {
                            LogManager.getLogger().info("YEAH BABY!");
                        }));
    }

    public TankScreen(TankContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = this.guiLeft;
        int j = this.guiTop;

        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

        this.blit(matrixStack, i + 35, j + 8,  176, 0,
                16, 68 - getProgressScaled(68, container.getTankLevel(Direction.WEST)));
        this.blit(matrixStack, i + 125, j + 8, 176, 0,
                16, 68  - getProgressScaled(68, container.getTankLevel(Direction.EAST)));

        this.blit(matrixStack, i + 35, j + 8, 192, 0,
                16, 68);
        this.blit(matrixStack, i + 125, j + 8, 192, 0,
                16, 68);
    }

    private int getProgressScaled(int scale, int progress)
    {
        int top = 10;

        if(progress != top)
        {
            return progress * (scale / top);
        }
        else
        {
            return scale;
        }
    }
}
