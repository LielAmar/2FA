package com.lielamar.auth.bukkit.utils.map;

import com.lielamar.auth.bukkit.utils.nayukifast.QrCode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URI;

public class ImageRender extends MapRenderer {

    private final SoftReference<BufferedImage> cacheImage;
    private boolean hasRendered = false;

    public ImageRender(String uri) {
        this.cacheImage = new SoftReference<>(this.generateQrCodeImage(uri));
    }

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (this.hasRendered) return;

        view.setScale(MapView.Scale.CLOSEST);

        if (this.cacheImage != null && this.cacheImage.get() != null)
            canvas.drawImage(0, 0, this.cacheImage.get());
        else
            player.sendMessage(ChatColor.RED + "Attempted to render the image, but the cached image was null!");

        this.hasRendered = true;
    }

    private BufferedImage generateQrCodeImage(String uri) {
        QrCode qr = QrCode.encodeText(uri, QrCode.Ecc.MEDIUM);

        int scale = 4;
        int border = 2;

        int size = (qr.size + border * 2) * scale;

        BufferedImage image;
        boolean originalUseCache = ImageIO.getUseCache();
        ImageIO.setUseCache(false);

        try {
            image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);  // Fill the entire image with white
            g.fillRect(0, 0, size, size);

            g.setColor(Color.BLACK);  // Draw the QR code in black
            for (int y = 0; y < qr.size; y++) {
                for (int x = 0; x < qr.size; x++) {
                    if (qr.getModule(x, y)) {
                        g.fillRect((x + border) * scale, (y + border) * scale, scale, scale);
                    }
                }
            }

            g.dispose();
        } finally {
            ImageIO.setUseCache(originalUseCache);
        }

        return image;
    }
}
