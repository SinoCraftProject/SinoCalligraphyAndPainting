package games.moegirl.sinocraft.sinocalligraphy.client.paper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import games.moegirl.sinocraft.sinocalligraphy.gui.BrushGuiScreen;
import games.moegirl.sinocraft.sinocalligraphy.item.SCAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

/**
 * BlockEntity Without Level Renderer is the new ISTER.
 * A mod should only one BEWLR.
 * @author qyl27
 */
@OnlyIn(Dist.CLIENT)
public class FilledXuanPaperBlockRenderer extends BlockEntityWithoutLevelRenderer {

    private static FilledXuanPaperBlockRenderer INSTANCE = null;

    public synchronized static FilledXuanPaperBlockRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilledXuanPaperBlockRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }
        return INSTANCE;
    }

    FilledXuanPaperBlockRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderXuanPaperAsItem(stack, transformType, poseStack, buffer, packedLight);
    }

    private void renderXuanPaperAsItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int light) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        poseStack.pushPose();
        if (transformType == ItemTransforms.TransformType.FIXED) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            poseStack.scale(1, -1, 1);
            poseStack.translate(-1.5, -1.5, -0.5);
            poseStack.scale(0.0625f, 0.0625f, 0.0625f);
            poseStack.translate(0.0D, 0.0D, 0.01D);
        } else {
            poseStack.scale(0.03125f, 0.03125f, 1.0f);
            poseStack.scale(BrushGuiScreen.CANVAS_SIZE, BrushGuiScreen.CANVAS_SIZE, BrushGuiScreen.CANVAS_SIZE);
        }
        renderXuanPaper(poseStack, buffer, light, stack);
        poseStack.popPose();
    }

    public static void renderXuanPaper(PoseStack stack, MultiBufferSource buffer, int combinedLight, ItemStack item) {
        if (!item.is(SCAItems.XUAN_PAPER.get())) {
            return;
        }
        byte[] pixels;
        if (item.hasTag() && item.getOrCreateTag().contains(BrushGuiScreen.PIXELS_TAG_NAME, Tag.TAG_BYTE_ARRAY)) {
            pixels = item.getOrCreateTag().getByteArray(BrushGuiScreen.PIXELS_TAG_NAME);
        } else {
            pixels = new byte[BrushGuiScreen.CANVAS_SIZE * BrushGuiScreen.CANVAS_SIZE];
            Arrays.fill(pixels, (byte) 0);
        }


        VertexConsumer vertex = buffer.getBuffer(RenderType.create(
                BrushGuiScreen.PIXELS_TAG_NAME, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorLightmapShader))
                        .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                        .createCompositeState(false)));
        for (int x1 = 0; x1 < BrushGuiScreen.CANVAS_SIZE; x1++) {
            float x2 = x1 + 1;
            for (int y1 = 0; y1 < BrushGuiScreen.CANVAS_SIZE; y1++) {
                /** range 0-1 */
                float pixel = 0.0625f * (16 - pixels[x1 * BrushGuiScreen.CANVAS_SIZE + y1]);
                float y2 = -~y1;
                // #f9f4dc
                float r=pixel*0.947306f;
                float g=pixel*0.90466f;
                float b=pixel*0.715694f;
                //int grayScale=(int)pixel*255;
                //int pColor = FastColor.ARGB32.multiply(FastColor.ARGB32.color(255,249,244,220), FastColor.ARGB32.color(255,grayScale,grayScale,grayScale));
                vertex.vertex(stack.last().pose(), x1, y1, 0).color(r,g,b, 1).uv2(combinedLight).endVertex();
                vertex.vertex(stack.last().pose(), x1, y2, 0).color(r,g,b, 1).uv2(combinedLight).endVertex();
                vertex.vertex(stack.last().pose(), x2, y2, 0).color(r,g,b, 1).uv2(combinedLight).endVertex();
                vertex.vertex(stack.last().pose(), x2, y1, 0).color(r,g,b, 1).uv2(combinedLight).endVertex();
            }
        }
    }
}
