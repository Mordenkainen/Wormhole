package com.mordenkainen.wormhole.render.tileentity;

// Java
import java.util.Map;

// Minecraft
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

// OpenGL
import org.lwjgl.opengl.GL11;

// Wormhole
import com.mordenkainen.wormhole.blocks.BlockEnum;
import com.mordenkainen.wormhole.tileentity.TileEntityPlayerLink;

public class TileEntityRendererPlayerLink extends TileEntitySpecialRenderer {	
	@SuppressWarnings("rawtypes")
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float tick) {
		float pixelx = (float)1/64;
		float pixely = (float)1/32;
		ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
		if (((TileEntityPlayerLink)tileentity).owner != null && ((TileEntityPlayerLink)tileentity).getCamo() == null) {
			Minecraft minecraft = Minecraft.getMinecraft();
            Map map = minecraft.func_152342_ad().func_152788_a(((TileEntityPlayerLink)tileentity).owner);
            
            if (map.containsKey(Type.SKIN)) {
            	resourcelocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
            }
            bindTexture(resourcelocation);
            GL11.glPushMatrix();
    		GL11.glTranslated(x, y, z);
    		Tessellator tessellator = Tessellator.instance;
    		tessellator.startDrawingQuads();
    		tessellator.setBrightness(BlockEnum.PLAYERLINK.getBlockInstance().getMixedBrightnessForBlock(tileentity.getWorldObj(), tileentity.xCoord, tileentity.yCoord, tileentity.zCoord + 1));
    		tessellator.addVertexWithUV(0.25, 0.25, -0.001, 16 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(0.25, 0.75, -0.001, 16 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(0.75, 0.75, -0.001, 8 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(0.75, 0.25, -0.001, 8 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(1.001, 0.25, 0.25, 16 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(1.001, 0.75, 0.25, 16 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(1.001, 0.75, 0.75, 8 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(1.001, 0.25, 0.75, 8 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(0.75, 0.25, 1.001, 16 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(0.75, 0.75, 1.001, 16 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(0.25, 0.75, 1.001, 8 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(0.25, 0.25, 1.001, 8 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(-0.001, 0.25, 0.75, 16 * pixelx, 16 * pixely);
    		tessellator.addVertexWithUV(-0.001, 0.75, 0.75, 16 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(-0.001, 0.75, 0.25, 8 * pixelx, 8 * pixely);
    		tessellator.addVertexWithUV(-0.001, 0.25, 0.25, 8 * pixelx, 16 * pixely);
    		tessellator.draw();
    		GL11.glPopMatrix();
		}
	}
}
