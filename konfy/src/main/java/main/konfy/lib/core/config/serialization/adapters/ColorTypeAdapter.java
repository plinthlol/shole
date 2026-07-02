package main.konfy.lib.core.config.serialization.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import main.konfy.lib.core.config.local.options.type.KonfyLibColor;

public class ColorTypeAdapter extends TypeAdapter<KonfyLibColor> {
   public void write(JsonWriter out, KonfyLibColor color) throws IOException {
      if (color == null) {
         out.nullValue();
      } else {
         out.beginObject();
         out.name("r").value(color.getRed());
         out.name("g").value(color.getGreen());
         out.name("b").value(color.getBlue());
         out.name("a").value(color.getAlpha());
         out.name("value").value(color.getRGB());
         out.name("hue").value(color.getHue());
         out.name("saturation").value(color.getSaturation());
         out.name("brightness").value(color.getBrightness());
         out.name("rainbow").value(color.isRainbow());
         out.name("rainbowSpeed").value(color.getRainbowSpeed());
         out.name("pulse").value(color.isPulse());
         out.name("pulseSpeed").value(color.getPulseSpeed());
         out.endObject();
      }
   }

   public KonfyLibColor read(JsonReader in) throws IOException {
      int r = 0;
      int g = 0;
      int b = 0;
      int a = 255;
      int value = 0;
      float hue = 0.0F;
      float saturation = 0.0F;
      float brightness = 0.0F;
      boolean rainbow = false;
      boolean pulse = false;
      int rainbowSpeed = 5;
      int pulseSpeed = 5;
      in.beginObject();

      while (in.hasNext()) {
         switch (in.nextName()) {
            case "r":
               r = in.nextInt();
               break;
            case "g":
               g = in.nextInt();
               break;
            case "b":
               b = in.nextInt();
               break;
            case "a":
               a = in.nextInt();
               break;
            case "value":
               value = in.nextInt();
               break;
            case "hue":
               hue = (float)in.nextDouble();
               break;
            case "saturation":
               saturation = (float)in.nextDouble();
               break;
            case "brightness":
               brightness = (float)in.nextDouble();
               break;
            case "rainbow":
               rainbow = in.nextBoolean();
               break;
            case "rainbowSpeed":
               rainbowSpeed = in.nextInt();
               break;
            case "pulse":
               pulse = in.nextBoolean();
               break;
            case "pulseSpeed":
               pulseSpeed = in.nextInt();
               break;
            default:
               in.skipValue();
         }
      }

      in.endObject();
      KonfyLibColor color = new KonfyLibColor(r, g, b, a);
      color.setHue(hue);
      color.setSaturation(saturation);
      color.setBrightness(brightness);
      color.setRainbow(rainbow);
      color.setRainbowSpeed(rainbowSpeed);
      color.setPulse(pulse);
      color.setPulseSpeed(pulseSpeed);
      return color;
   }
}
