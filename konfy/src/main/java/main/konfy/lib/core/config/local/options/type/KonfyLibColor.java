package main.konfy.lib.core.config.local.options.type;

public class KonfyLibColor implements Tickable {
   private int value;
   private float hue = 0.0F;
   private float saturation;
   private float brightness;
   private boolean rainbow = false;
   private int rainbowSpeed = 5;
   private int pulseSpeed = 5;
   private boolean pulse = false;
   private float pulseTime = 0.0F;

   public KonfyLibColor(int r, int g, int b) {
      this(r, g, b, 255);
   }

   public KonfyLibColor(int r, int g, int b, int a) {
      this.value = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
      float[] hsb = RGBtoHSB(r, g, b, null);
      this.hue = hsb[0];
      this.saturation = hsb[1];
      this.brightness = hsb[2];
   }

   public KonfyLibColor(int rgb) {
      this.value = 0xFF000000 | rgb;
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      float[] hsb = RGBtoHSB(r, g, b, null);
      this.hue = hsb[0];
      this.saturation = hsb[1];
      this.brightness = hsb[2];
   }

   public boolean isRainbow() {
      return this.rainbow;
   }

   public void setHue(float hue) {
      this.hue = Math.max(0.0F, Math.min(1.0F, hue));
   }

   public float getHue() {
      return this.hue;
   }

   public void setSaturation(float saturation) {
      this.saturation = Math.max(0.0F, Math.min(1.0F, saturation));
   }

   public float getSaturation() {
      return this.saturation;
   }

   public void setBrightness(float brightness) {
      this.brightness = Math.max(0.0F, Math.min(1.0F, brightness));
   }

   public float getBrightness() {
      return this.brightness;
   }

   public int getRainbowSpeed() {
      return this.rainbowSpeed;
   }

   public void setRainbow(boolean rainbow) {
      this.rainbow = rainbow;
   }

   public void setRainbowSpeed(int rainbowSpeed) {
      this.rainbowSpeed = rainbowSpeed;
   }

   public int getPulseSpeed() {
      return this.pulseSpeed;
   }

   public void setPulseSpeed(int pulseSpeed) {
      this.pulseSpeed = pulseSpeed;
   }

   public boolean isPulse() {
      return this.pulse;
   }

   public void setPulse(boolean pulse) {
      this.pulse = pulse;
   }

   public void setAlpha(int alpha) {
      KonfyLibColor newColor = new KonfyLibColor(this.getRed(), this.getGreen(), this.getBlue(), alpha);
      this.value = newColor.getRGB();
   }

   public void resetHSB() {
      float[] hsb = RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null);
      this.hue = hsb[0];
      this.saturation = hsb[1];
      this.brightness = hsb[2];
   }

   public void resetAdditions() {
      this.pulse = false;
      this.rainbow = false;
      this.rainbowSpeed = 5;
      this.pulseSpeed = 5;
   }

   public KonfyLibColor.Additions getAdditions() {
      return new KonfyLibColor.Additions(this.hue, this.saturation, this.brightness, this.rainbow, this.rainbowSpeed, this.pulse, this.pulseSpeed);
   }

   public void setAdditions(KonfyLibColor.Additions additions) {
      this.setHue(additions.hue());
      this.setSaturation(additions.saturation());
      this.setBrightness(additions.brightness());
      this.setRainbow(additions.rainbow());
      this.setRainbowSpeed(additions.rainbowSpeed());
      this.setPulse(additions.pulse());
      this.setPulseSpeed(additions.pulseSpeed());
   }

   @Override
   public void tick() {
      if (this.rainbow) {
         float speed = this.rainbowSpeed / 1000.0F;
         this.hue += speed;
         if (this.hue > 1.0F) {
            this.hue = 0.0F;
         }

         KonfyLibColor newColor = getHSBColor(this.hue, this.saturation, this.brightness);
         KonfyLibColor newColorAlpha = new KonfyLibColor(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), this.getAlpha());
         this.value = newColorAlpha.getRGB();
      }

      this.handlePulse();
   }

   private void handlePulse() {
      if (this.pulse) {
         this.pulseTime = this.pulseTime + this.pulseSpeed / 1000.0F;
         this.brightness = (float)((Math.sin(this.pulseTime * 2.0F * Math.PI) + 1.0) / 2.0);
         KonfyLibColor newColor = getHSBColor(this.hue, this.saturation, this.brightness);
         KonfyLibColor newColorAlpha = new KonfyLibColor(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), this.getAlpha());
         this.value = newColorAlpha.getRGB();
      }
   }

   public static KonfyLibColor getHSBColor(float h, float s, float b) {
      return new KonfyLibColor(HSBtoRGB(h, s, b));
   }

   public int getRGB() {
      return this.value;
   }

   public int getRed() {
      return this.getRGB() >> 16 & 0xFF;
   }

   public int getGreen() {
      return this.getRGB() >> 8 & 0xFF;
   }

   public int getBlue() {
      return this.getRGB() >> 0 & 0xFF;
   }

   public int getAlpha() {
      return this.getRGB() >> 24 & 0xFF;
   }

   public static int HSBtoRGB(float hue, float saturation, float brightness) {
      int r = 0;
      int g = 0;
      int b = 0;
      if (saturation == 0.0F) {
         r = g = b = (int)(brightness * 255.0F + 0.5F);
      } else {
         float h = (hue - (float)Math.floor(hue)) * 6.0F;
         float f = h - (float)Math.floor(h);
         float p = brightness * (1.0F - saturation);
         float q = brightness * (1.0F - saturation * f);
         float t = brightness * (1.0F - saturation * (1.0F - f));
         switch ((int)h) {
            case 0:
               r = (int)(brightness * 255.0F + 0.5F);
               g = (int)(t * 255.0F + 0.5F);
               b = (int)(p * 255.0F + 0.5F);
               break;
            case 1:
               r = (int)(q * 255.0F + 0.5F);
               g = (int)(brightness * 255.0F + 0.5F);
               b = (int)(p * 255.0F + 0.5F);
               break;
            case 2:
               r = (int)(p * 255.0F + 0.5F);
               g = (int)(brightness * 255.0F + 0.5F);
               b = (int)(t * 255.0F + 0.5F);
               break;
            case 3:
               r = (int)(p * 255.0F + 0.5F);
               g = (int)(q * 255.0F + 0.5F);
               b = (int)(brightness * 255.0F + 0.5F);
               break;
            case 4:
               r = (int)(t * 255.0F + 0.5F);
               g = (int)(p * 255.0F + 0.5F);
               b = (int)(brightness * 255.0F + 0.5F);
               break;
            case 5:
               r = (int)(brightness * 255.0F + 0.5F);
               g = (int)(p * 255.0F + 0.5F);
               b = (int)(q * 255.0F + 0.5F);
         }
      }

      return 0xFF000000 | r << 16 | g << 8 | b << 0;
   }

   public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
      if (hsbvals == null) {
         hsbvals = new float[3];
      }

      int cmax = r > g ? r : g;
      if (b > cmax) {
         cmax = b;
      }

      int cmin = r < g ? r : g;
      if (b < cmin) {
         cmin = b;
      }

      float brightness = cmax / 255.0F;
      float saturation;
      if (cmax != 0) {
         saturation = (float)(cmax - cmin) / cmax;
      } else {
         saturation = 0.0F;
      }

      float hue;
      if (saturation == 0.0F) {
         hue = 0.0F;
      } else {
         float redc = (float)(cmax - r) / (cmax - cmin);
         float greenc = (float)(cmax - g) / (cmax - cmin);
         float bluec = (float)(cmax - b) / (cmax - cmin);
         if (r == cmax) {
            hue = bluec - greenc;
         } else if (g == cmax) {
            hue = 2.0F + redc - bluec;
         } else {
            hue = 4.0F + greenc - redc;
         }

         hue /= 6.0F;
         if (hue < 0.0F) {
            hue++;
         }
      }

      hsbvals[0] = hue;
      hsbvals[1] = saturation;
      hsbvals[2] = brightness;
      return hsbvals;
   }

   public KonfyLibColor copy() {
      KonfyLibColor copy = new KonfyLibColor(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
      copy.hue = this.hue;
      copy.saturation = this.saturation;
      copy.brightness = this.brightness;
      copy.rainbow = this.rainbow;
      copy.rainbowSpeed = this.rainbowSpeed;
      copy.pulse = this.pulse;
      copy.pulseSpeed = this.pulseSpeed;
      copy.pulseTime = this.pulseTime;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof KonfyLibColor other) {
         if (this.rainbowSpeed != other.rainbowSpeed) {
            return false;
         } else if (this.rainbow != other.rainbow) {
            return false;
         } else if (this.pulseSpeed != other.pulseSpeed) {
            return false;
         } else if (this.pulse != other.pulse) {
            return false;
         } else if (this.brightness != other.brightness && !this.pulse) {
            return false;
         } else {
            return this.saturation != other.saturation ? false : this.value == other.value || this.rainbow || this.pulse;
         }
      } else {
         return false;
      }
   }

   public record Additions(float hue, float saturation, float brightness, boolean rainbow, int rainbowSpeed, boolean pulse, int pulseSpeed) {
   }
}
