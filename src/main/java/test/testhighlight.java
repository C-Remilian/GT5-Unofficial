package test;

import java.util.ArrayList;
import java.util.List;

import appeng.client.render.highlighter.HighlighterManager;
import appeng.client.render.highlighter.IHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import appeng.api.util.DimensionalCoord;

// stolen from Applied Energistics for debug purposes only
public class testhighlight implements IHighlighter {
    static final testhighlight INSTANCE;
    static {
        INSTANCE = new testhighlight();
        HighlighterManager.HIGHLIGHTERS.add(INSTANCE);
    }

    protected final List<DimensionalCoord> highlightedBlocks = new ArrayList<>();
    protected long expireHighlightTime;
    protected final int MIN_TIME = 3000;

    protected int dimension;
    protected double doubleX;
    protected double doubleY;
    protected double doubleZ;

    public static float R = 1f;
    public static float G = 0f;
    public static float B = 0f;

    testhighlight() {}

    public static void highlightBlocks(EntityPlayer player, List<DimensionalCoord> interfaces, String deviceName,
                                       String foundMsg, String wrongDimMsg) {
        INSTANCE.clear();
        for (DimensionalCoord coord : interfaces) {
            INSTANCE.highlightedBlocks.add(coord);
        }
        INSTANCE.expireHighlightTime = System.currentTimeMillis() + INSTANCE.MIN_TIME;
    }

    public static void highlightBlocks(EntityPlayer player, List<DimensionalCoord> interfaces, String foundMsg,
                                       String wrongDimMsg) {
        highlightBlocks(player, interfaces, "", foundMsg, wrongDimMsg);
    }

    public void clear() {
        highlightedBlocks.clear();
        expireHighlightTime = -1;
    }

    @Override
    public boolean noWork() {
        return highlightedBlocks.isEmpty();
    }

    public void renderHighlightedBlocks(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        dimension = mc.theWorld.provider.dimensionId;

        EntityPlayerSP p = mc.thePlayer;
        doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks;
        doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks;
        doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks;

        for (DimensionalCoord c : highlightedBlocks) {
            if (dimension != c.getDimension()) {
                continue;
            }
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glLineWidth(3);
            GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            renderHighLightedBlocksOutline(c.x, c.y, c.z);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    void renderHighLightedBlocksOutline(double x, double y, double z) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_LINE_STRIP);

        tess.setColorRGBA_F(R, G, B, 1.0f);

        tess.addVertex(x, y, z);
        tess.addVertex(x, y + 1, z);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x, y, z + 1);
        tess.addVertex(x, y, z);

        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x + 1, y, z);

        tess.addVertex(x, y, z);
        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x, y, z + 1);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x + 1, y, z);
        tess.addVertex(x, y, z);
        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x, y + 1, z);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x, y, z + 1);

        tess.draw();
    }

    @Override
    public long getExpireTime() {
        return expireHighlightTime;
    }
}
