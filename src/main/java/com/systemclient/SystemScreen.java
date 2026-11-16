package com.systemclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SystemScreen extends Screen {
    private String page = "Combat";
    private String waitingBind = null;
    private String selected = "Aim Assist";
    private String configName = "default";
    private final List<Row> rows = new ArrayList<>();
    private record Row(int x, int y, int w, int h, String action) {}
    private final int accent = 0xFF3358D4;
    public SystemScreen() { super(Component.literal("System Client")); }
    @Override public void render(GuiGraphics g, int mx, int my, float delta) {
        int w = 950, h = 560, x = (width-w)/2, y=(height-h)/2;
        g.fill(0,0,width,height,0x99000000); g.fill(x,y,x+w,y+h,0xFF101112); g.fill(x+1,y+1,x+w-1,y+h-1,0xFF151617);
        g.fill(x+12,y+12,x+190,y+h-12,0xFF0D0E0F); g.drawString(font,"✧ System",x+28,y+25,0xFFFFFFFF,false);
        String[] nav={"All","Combat","Movement","Player","Render","Visual","Client","Configs","Friends","Waypoints","Settings","Search"};
        int ny=y+75; for(String n:nav){ boolean active=page.equals(n); if(active) g.fill(x+20,ny-5,x+180,ny+20,0xFF292B2D); g.drawString(font,n,x+34,ny,active?0xFFFFFFFF:0xFFAAAAAA,false); ny+=34; if(n.equals("Client")) ny+=18; }
        g.fill(x+205,y+12,x+w-12,y+52,0xFF0D0E0F); g.drawString(font,"▣   "+page,x+225,y+27,0xFFFFFFFF,false);
        g.drawString(font,"System Client  •  Right Shift: close",x+w-265,y+28,0xFF888888,false);
        rows.clear();
        if(page.equals("Configs")) renderConfigs(g,x,y);
        else renderCombat(g,x,y);
        super.render(g,mx,my,delta);
    }
    private void renderCombat(GuiGraphics g,int x,int y){
        int left=x+215, top=y+72, col=260;
        g.drawString(font,"Combat modules",left,top,0xFFFFFFFF,false);
        int yy=top+25;
        for(Map.Entry<String,Config.Module> e:Config.MODULES.entrySet()){
            Config.Module m=e.getValue();
            g.fill(left,yy,left+col,yy+42,0xFF222426); g.drawString(font,e.getKey(),left+12,yy+8,0xFFFFFFFF,false);
            g.drawString(font,m.enabled?"ON":"OFF",left+col-48,yy+8,m.enabled?accent:0xFF999999,false);
            g.drawString(font,"MMB: bind  •  "+(m.bind<0?"unbound":GLFW.glfwGetKeyName(m.bind,0)),left+12,yy+25,0xFF999999,false);
            rows.add(new Row(left,yy,col,42,"module:"+e.getKey())); yy+=50;
        }
        Config.Module m=Config.MODULES.get(selected);
        int rx=x+500, rw=420; g.drawString(font,"Settings  /  "+selected,rx,top,0xFFFFFFFF,false);
        yy=top+25;
        if(m!=null){
            row(g,rx,yy,rw,"Target type: "+m.targetMode,"mode"); yy+=45;
            row(g,rx,yy,rw,"Aim speed: "+(int)m.speed+"%","speed"); yy+=45;
            row(g,rx,yy,rw,"Aim prediction: "+(int)m.prediction+"%","prediction"); yy+=45;
            row(g,rx,yy,rw,"FPS Adaptive: "+(m.fpsAdaptive?"ON":"OFF"),"fps"); yy+=45;
            row(g,rx,yy,rw,"Trigger delay: "+(int)m.delay+" ms","delay"); yy+=45;
            row(g,rx,yy,rw,"Trigger prediction: "+(int)m.triggerPrediction+"%","tpred"); yy+=45;
            g.drawString(font,waitingBind!=null?"Press a key to bind "+waitingBind:"Middle-click a module to bind it",rx,yy+12,0xFFAAAAAA,false);
        }
       g.drawString(font,"UI scaffold: controls and configs are ready for your server-specific logic.",left, y+528,0xFF888888,false);
    }
    private void row(GuiGraphics g,int x,int y,int w,String text,String action){g.fill(x,y,x+w,y+36,0xFF222426);g.drawString(font,text,x+12,y+12,0xFFE8E8E8,false);rows.add(new Row(x,y,w,36,action));}
    private void renderConfigs(GuiGraphics g,int x,int y){
        int rx=x+235, yy=y+105; g.drawString(font,"Configs",rx,yy,0xFFFFFFFF,false);
        g.drawString(font,"Default config is saved automatically. Named configs use separate JSON files.",rx,yy+28,0xFFAAAAAA,false);
        g.fill(rx,yy+65,rx+420,yy+105,0xFF222426);g.drawString(font,"Name: "+configName,rx+12,yy+79,0xFFFFFFFF,false);rows.add(new Row(rx,yy+65,420,40,"name"));
        row(g,rx,yy+120,200,"Save config","save"); row(g,rx+215,yy+120,200,"Load config","load");
        g.drawString(font,"File location: .minecraft/config/systemclient/",rx,yy+180,0xFF999999,false);
    }
    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick){
        double mx=event.x(), my=event.y(); int button=event.button();
        for(Row r:rows) if(mx>=r.x&&mx<=r.x+r.w&&my>=r.y&&my<=r.y+r.h){
            String a=r.action;
            if(a.startsWith("module:")){selected=a.substring(7); if(button==2) waitingBind=selected; else Config.MODULES.get(selected).enabled=!Config.MODULES.get(selected).enabled; Config.save(); return true;}
            Config.Module m=Config.MODULES.get(selected); if(m==null) return true;
            switch(a){case "mode" -> m.targetMode=m.targetMode.equals("Players")?"Mobs":m.targetMode.equals("Mobs")?"All":"Players";case "speed" -> m.speed=m.speed>=100?10:m.speed+10;case "prediction" -> m.prediction=m.prediction>=100?0:m.prediction+10;case "fps" -> m.fpsAdaptive=!m.fpsAdaptive;case "delay" -> m.delay=m.delay>=200?50:m.delay+25;case "tpred" -> m.triggerPrediction=m.triggerPrediction>=100?0:m.triggerPrediction+10;case "save" -> Config.saveAs(configName);case "load" -> Config.loadAs(configName);case "name" -> configName=configName.equals("default")?"server":"default";}
            Config.save(); return true;
        }
        // Sidebar navigation
        int sx=(width-950)/2+20, sy=(height-560)/2+70; String[] nav={"All","Combat","Movement","Player","Render","Visual","Client","Configs","Friends","Waypoints","Settings","Search"};
        for(String n:nav){if(mx>=sx&&mx<=sx+160&&my>=sy-5&&my<=sy+20){page=n;return true;}sy+=34;if(n.equals("Client"))sy+=18;}
        return super.mouseClicked(event,doubleClick);
    }
    @Override public boolean keyPressed(KeyEvent event){
        int key=event.key();
        if(waitingBind!=null){Config.MODULES.get(waitingBind).bind=key;waitingBind=null;Config.save();return true;}
        if(key==GLFW.GLFW_KEY_ESCAPE||key==GLFW.GLFW_KEY_RIGHT_SHIFT){onClose();return true;}
        return super.keyPressed(event);
    }
    @Override public void onClose(){Config.save();Minecraft.getInstance().setScreen(null);}
    @Override public boolean isPauseScreen(){return false;}
}
