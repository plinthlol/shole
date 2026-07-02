package main.konfy.lib.core.gui.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.OptionDescription;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.popup.PopUp;
import main.konfy.lib.core.gui.popup.impl.SaveWarningPopUp;
import main.konfy.lib.core.gui.utils.CategoryTab;
import main.konfy.lib.core.gui.utils.TabLocation;
import main.konfy.lib.core.gui.widgets.ButtonWidget;
import main.konfy.lib.core.gui.widgets.OpenableWidget;
import main.konfy.lib.core.gui.widgets.OptionGroupWidget;
import main.konfy.lib.core.gui.widgets.OptionWidget;
import main.konfy.lib.core.gui.widgets.PixelGridAnimationWidget;
import main.konfy.lib.core.gui.widgets.ScrollableTabWidget;
import main.konfy.lib.core.gui.widgets.SearchBarWidget;
import main.konfy.lib.core.gui.widgets.ModWidget;
import main.konfy.lib.core.gui.widgets.StringListOptionWidget;
import main.konfy.lib.core.mods.Mod;
import main.konfy.lib.core.mods.ModEntryPointList;
import main.konfy.lib.core.manager.KonfyLibConfigManager;
import main.konfy.lib.core.mixin.ScreenAccessor;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class KonfyLibConfigScreen extends BaseScreen {
   private final TabManager tabManager;
   private final List<OptionGroupWidget> allGroupWidgets;
   private final List<OptionWidget> allOptionWidgets;
   private final List<CategoryTab> allTabs;
   private float scrollAmount = 0.0F;
   private final String name;
   private final KonfyLibConfigManager configManager;
   private ScrollableTabWidget tabWidget;
   private ButtonWidget doneButton;
   private ButtonWidget applyButton;
   private ButtonWidget undoButton;
   private ButtonWidget backButton;
   private SearchBarWidget searchBar;
   private Option<?> focusedOption;
   private Graphics currentGraphicsContext;
   public PopUp popUp = null;
   private int maxScroll = 0;
   public boolean scroll = true;
   protected boolean showModsTab = false;
   protected final List<ModWidget> modWidgets = new ArrayList<>();
   protected final ModEntryPointList entryPointList = new ModEntryPointList();

   public KonfyLibConfigScreen(Screen parent, ModConfig config, String title) {
      super(title + " Config Screen", parent);
      this.tabManager = new TabManager(x$0 -> this.addRenderableWidget(x$0), x$0 -> this.removeWidget(x$0));
      this.allGroupWidgets = new ArrayList<>();
      this.allOptionWidgets = new ArrayList<>();
      this.allTabs = new ArrayList<>();
      this.name = title;
      this.configManager = new KonfyLibConfigManager(config);
      this.focusedOption = null;
      this.currentGraphicsContext = null;
      this.entryPointList.retrieve();
   }

   @Override
   public void onClose() {
      if (this.popUp == null || this.popUp.canClose()) {
         if (this.shouldUndoOptions() && this.popUp == null) {
            this.popUp = new SaveWarningPopUp(this, () -> {
               this.save(true);
               super.onClose();
            }, () -> {
               this.undo();
               super.onClose();
            }, () -> this.popUp.close());
          } else {
             super.onClose();
             this.save(false);
          }
      }
   }

    protected void init() {
       this.defineOptions();
       this.setOptionPrevs();
       super.init();
       this.initButtons();
       this.initSearchBar();
       this.initTabs();
       this.rebuildWidgets();

       boolean hasUnsaved = this.shouldUndoOptions();
       this.applyButton.setEnabled(hasUnsaved);
       this.undoButton.visible = hasUnsaved;
       this.undoButton.active = hasUnsaved;
       this.doneButton.setEnabled(!hasUnsaved);
       this.updateWidgetVisibilities();
    }

   private void initButtons() {
       this.doneButton = new ButtonWidget(this.width - 58, this.height - 21, 50, 16, true, "Done", () -> {
          if (this.shouldUndoOptions()) {
             this.save(true);
          }
          super.onClose();
       });
       this.applyButton = new ButtonWidget(this.width - 58 - 55, this.height - 21, 50, 16, true, "Apply", () -> this.save(true));
       this.undoButton = new ButtonWidget(this.width - 58 - 110, this.height - 21, 50, 16, true, "Undo", this::undo);
       this.backButton = new ButtonWidget(this.width - 58, this.height - 21, 50, 16, true, "Back", this::onClose);
       this.backButton.visible = false;
       this.addRenderableWidget(this.doneButton);
       this.addRenderableWidget(this.applyButton);
       this.addRenderableWidget(this.undoButton);
       this.addRenderableWidget(this.backButton);
   }

   private void initSearchBar() {
      this.searchBar = new SearchBarWidget(this, 8, this.height - 22, 102, 18, this::search);
      this.addRenderableWidget(this.searchBar);
   }

    private void initTabs() {
       this.updateScreenGlobals();
       List<CategoryTab> tabList = new ArrayList<>();
 
       if (this.showModsTab) {
          Category modsCategory = new Category("Mods", List.of(), List.of());
          tabList.add(new CategoryTab(modsCategory, List.of()));
       }
 
       for (Category category : this.configManager.get().categories()) {
          List<OptionGroupWidget> groupWidgets = new ArrayList<>();
          int yOffset = 34;
 
          for (OptionGroup group : category.optionGroups()) {
             int groupH = ScreenGlobals.OPTION_HEIGHT;
             int groupHeight = groupH;
             OptionGroupWidget groupWidget = new OptionGroupWidget(
                (this.width - (ScreenGlobals.OPTION_PANEL_ENDX - ScreenGlobals.OPTION_PANEL_STARTX)) / 2, yOffset, 150, groupH, group, this
             );
             if (group.isExpanded()) {
                int childY = yOffset + groupH;
                List<OptionWidget> children = groupWidget.getChildren();
 
                for (int i = 0; i < children.size(); i++) {
                   OptionWidget child = children.get(i);
                   child.setPosition(child.getX(), childY);
                   childY += ScreenGlobals.OPTION_HEIGHT;
                   groupHeight += ScreenGlobals.OPTION_HEIGHT;
                   if (i < children.size() - 1) {
                      childY += ScreenGlobals.OPTION_GROUP_SEPARATION;
                      groupHeight += ScreenGlobals.OPTION_GROUP_SEPARATION;
                   }
                }
             }
 
             groupWidget.setHeight(groupHeight);
             groupWidgets.add(groupWidget);
             this.allGroupWidgets.add(groupWidget);
             this.allOptionWidgets.addAll(groupWidget.getChildren());
             yOffset += groupHeight + 10;
          }
 
          CategoryTab tab = new CategoryTab(category, groupWidgets);
          tabList.add(tab);
          this.allTabs.add(tab);
       }
 
       this.tabWidget = new ScrollableTabWidget(0, 0, this.width, 24, tabList, this.tabManager, TabLocation.TOP, this);
       this.addRenderableWidget(this.tabWidget);
       this.tabWidget.selectTab(0, true);
    }

   public void layoutGroupWidgets() {
       if (this.tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
          if (this.showModsTab && categoryTab.getCategory().name().equals("Mods")) {
             this.maxScroll = 0;
             this.scrollAmount = 0.0F;
             return;
          }
          List<OptionGroupWidget> var13 = categoryTab.getOptionGroupWidgets();
          int contentYOffset = 34;
 
          for (OptionGroupWidget group : var13) {
             if (group.visible) {
                int groupHeight = ScreenGlobals.OPTION_HEIGHT;
                if (group.getGroup().isExpanded()) {
                   List<OptionWidget> children = group.getChildren();
 
                   for (int i = 0; i < children.size(); i++) {
                      OptionWidget child = children.get(i);
                      if (child.isVisible()) {
                         groupHeight += this.getChildHeight(child);
                         if (this.hasNextVisible(children, i)) {
                            groupHeight += ScreenGlobals.OPTION_GROUP_SEPARATION;
                         }
                      }
                   }
                }
 
                group.setHeight(groupHeight);
                contentYOffset += groupHeight + 10;
             }
          }
 
          int viewHeight = this.height - 62;
          this.maxScroll = Math.max(0, contentYOffset - 10 * var13.size() - viewHeight);
          if (this.scrollAmount > this.maxScroll) {
             this.scrollAmount = Math.max(0, this.maxScroll);
          }
 
          int yOffset = (int)(34.0F - this.scrollAmount);

         for (OptionGroupWidget group : var13) {
            if (group.visible) {
               group.setPosition(this.width / 2, yOffset);
               if (group.getGroup().isExpanded()) {
                  int childY = yOffset + ScreenGlobals.OPTION_HEIGHT;
                  List<OptionWidget> children = group.getChildren();

                  for (int i = 0; i < children.size(); i++) {
                     OptionWidget child = children.get(i);
                     if (child.isVisible()) {
                        int childHeight = this.getChildHeight(child);
                        int optionStartX = (this.width - ScreenGlobals.OPTION_WIDTH) / 2;
                        child.setPosition(optionStartX, childY);
                        child.setHeight(childHeight);
                        child.onWidgetUpdate(child.getX() + child.getWidth() - ScreenGlobals.OPTION_HEIGHT + 22, childY);
                        childY += childHeight;
                        if (this.hasNextVisible(children, i)) {
                           childY += ScreenGlobals.OPTION_GROUP_SEPARATION;
                        }
                     }
                  }
               }

               yOffset += group.getHeight() + 10;
            }
         }
      }
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
      this.currentGraphicsContext = new Graphics(extractor);
      super.extractRenderState(extractor, mouseX, mouseY, delta);
   }

    @Override
    protected void extract(Graphics graphics, int mouseX, int mouseY) {
       this.currentGraphicsContext = graphics;
       GuiGraphicsExtractor extractor = graphics.extractor();
       this.suppressWidgetMouse = this.popUp != null;
       extractor.horizontalLine(0, this.width, this.height - 28, MainColors.OUTLINE_BLACK.getRGB());
       extractor.horizontalLine(0, this.width, this.height - 27, MainColors.OUTLINE_WHITE.getRGB());

       boolean isMods = false;
       if (this.showModsTab && this.tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
          isMods = categoryTab.getCategory().name().equals("Mods");
       }

       this.backButton.visible = this.showModsTab && isMods;
       this.doneButton.visible = !this.showModsTab || !isMods;
       this.applyButton.visible = !this.showModsTab || !isMods;
       this.undoButton.visible = (!this.showModsTab || !isMods) && this.shouldUndoOptions();
       this.searchBar.visible = !this.showModsTab || !isMods;

       if (isMods) {
          for (ModWidget widget : this.modWidgets) {
             widget.extractRenderState(extractor, mouseX, mouseY, this.delta);
          }
          if (this.modWidgets.isEmpty()) {
             extractor.centeredText(this.getFont(), "No Mods", this.width / 2, this.height / 2, -1);
          }
       } else {
          boolean hasUnsaved = this.shouldUndoOptions();
          this.applyButton.setEnabled(hasUnsaved);
          if (this.applyButton.isHovered() && !this.applyButton.active) {
             this.setActiveTooltip("No changes to apply");
          }
          this.undoButton.visible = hasUnsaved;
          this.undoButton.active = hasUnsaved;
          this.doneButton.setEnabled(!hasUnsaved);
          if (this.doneButton.isHovered() && !this.doneButton.active) {
             this.setActiveTooltip("Apply your changes to finish");
          }
          
          this.layoutGroupWidgets();
          if (this.isConfigEmpty()) {
             extractor.centeredText(this.getFont(), "No Available Options...", this.width / 2, this.height / 2, -1);
          }
       }

       if (this.popUp != null) {
          extractor.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 100).getRGB());
          this.popUp.extract(graphics, mouseX, mouseY, this.delta);
       }
    }

    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
       if (this.popUp != null) {
          this.popUp.onClick(click, doubled);
       } else {
          boolean isMods = false;
          if (this.showModsTab && this.tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
             isMods = categoryTab.getCategory().name().equals("Mods");
          }

          if (isMods) {
             if (click.y() >= 24 && click.y() < this.height - 28) {
                for (ModWidget widget : this.modWidgets) {
                   widget.mouseClicked(click, doubled);
                }
             }
             if (this.backButton.isHovered()) {
                this.backButton.onClick(click, doubled);
             }
          } else if (!this.tabWidget.isHoveringOverAnyTab(click.x(), click.y())) {
             this.searchBar.setFocused(this.searchBar.isHovered());
             if (click.y() >= 24 && click.y() < this.height - 28) {
                ((ScreenAccessor)this).getDrawables().forEach(w -> {
                   if (w instanceof OptionGroupWidget optionGroupWidget) {
                      optionGroupWidget.onMouseClick(click, doubled);
                   } else if (w instanceof OptionWidget optionWidget) {
                      if (optionWidget.isVisible() && optionWidget.isInScissor(0, 24, this.width, this.height - 28) && optionWidget.isAvailable()) {
                         optionWidget.onMouseClick(click, doubled);
                      }

                      if (optionWidget.resetButton.active) {
                         optionWidget.resetButton.onClick(click, doubled);
                      }
                   }
                });
             }
          }
       }

       return super.mouseClicked(click, doubled);
    }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.popUp != null) {
         this.popUp.onMouseRelease(click);
      }

      ((ScreenAccessor)this).getDrawables().forEach(w -> {
         if (w instanceof OptionWidget optionWidget) {
            optionWidget.onMouseRelease(click);
         }
      });
      return super.mouseReleased(click);
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      if (this.popUp == null) {
         ((ScreenAccessor)this).getDrawables().forEach(w -> {
            if (w instanceof OptionWidget optionWidget && optionWidget.isAvailable()) {
               optionWidget.onMouseDrag(click, offsetX, offsetY);
            }
         });
      }

      return super.mouseDragged(click, offsetX, offsetY);
   }

   public void mouseMoved(double mouseX, double mouseY) {
      if (this.popUp == null) {
         ((ScreenAccessor)this).getDrawables().forEach(w -> {
            if (w instanceof OptionWidget optionWidget && optionWidget.isAvailable()) {
               optionWidget.onMouseMove(mouseX, mouseY);
            }
         });
      }

      super.mouseMoved(mouseX, mouseY);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.popUp != null) {
         this.popUp.onScroll(mouseX, mouseY, verticalAmount);
      } else if (this.scroll && !this.tabWidget.isHoveringOverAnyTab(mouseX, mouseY)) {
         this.scrollAmount = Math.max(0.0F, Math.min(this.scrollAmount - (float)(verticalAmount * 20.0), this.maxScroll));
      } else {
         ((ScreenAccessor)this).getDrawables().forEach(w -> {
            if (w instanceof OptionWidget optionWidget && optionWidget.isAvailable()) {
               optionWidget.onMouseScroll(mouseX, mouseY, verticalAmount);
            }
         });
      }

      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean keyPressed(KeyEvent input) {
      if (this.popUp == null) {
         ((ScreenAccessor)this).getDrawables().forEach(w -> {
            if (w instanceof OptionWidget optionWidget && optionWidget.isAvailable()) {
               optionWidget.onKeyPress(input);
            }
         });
      }

      return super.keyPressed(input);
   }

   public boolean charTyped(CharacterEvent input) {
      if (this.popUp == null) {
         ((ScreenAccessor)this).getDrawables().forEach(w -> {
            if (w instanceof OptionWidget optionWidget && optionWidget.isAvailable()) {
               optionWidget.onCharTyped(input);
            }
         });
      }

      return super.charTyped(input);
   }

    protected void rebuildWidgets() {
        if (this.tabWidget != null) {
           this.tabWidget.setWidth(this.width);
           this.tabWidget.setPosition(0, 0);
           int i = this.tabWidget.getRectangle().bottom();
           this.tabManager.setTabArea(new ScreenRectangle(0, i, this.width, this.height - 36 - i));
           this.layoutGroupWidgets();
        }
  
         this.doneButton.setPosition(this.width - 58, this.height - 21);
         this.applyButton.setPosition(this.width - 58 - 55, this.height - 21);
         this.undoButton.setPosition(this.width - 58 - 110, this.height - 21);
         if (this.backButton != null) {
            this.backButton.setPosition(this.width - 58, this.height - 21);
         }
         this.searchBar.setPosition(8, this.height - 22);
         this.searchBar.setWidth(102);
        this.updateScreenGlobals();
  
        int optionStartX = (this.width - ScreenGlobals.OPTION_WIDTH) / 2;
        for (OptionWidget widget : this.allOptionWidgets) {
           widget.setWidth(ScreenGlobals.OPTION_WIDTH);
           widget.setPosition(optionStartX, widget.getY());
           widget.onWidgetUpdate(widget.getX() + widget.getWidth() - ScreenGlobals.OPTION_HEIGHT + 22, widget.getY());
           if (widget instanceof OpenableWidget openableWidget && openableWidget.open) {
              openableWidget.setHeight(openableWidget.OPEN_HEIGHT);
           } else if (widget instanceof StringListOptionWidget stringListOptionWidget) {
              stringListOptionWidget.setHeight(ScreenGlobals.OPTION_HEIGHT + stringListOptionWidget.ADDITIONAL_HEIGHT);
           } else {
              widget.setHeight(ScreenGlobals.OPTION_HEIGHT);
           }
        }

        boolean isMods = false;
        if (this.showModsTab && this.tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
           isMods = categoryTab.getCategory().name().equals("Mods");
        }
        if (this.showModsTab && isMods) {
           this.setupModWidgets();
        }
        this.updateWidgetVisibilities();
    }

   @Override
   public void tick() {
      super.tick();

      for (OptionWidget widget : this.allOptionWidgets) {
         widget.tick();
      }
   }

      public void showWidgetsForCategory(Category category) {
         CategoryTab selected = (CategoryTab)this.tabManager.getCurrentTab();
         if (selected != null && selected.getCategory().name().equalsIgnoreCase(category.name())) {
            if (this.showModsTab && category.name().equals("Mods")) {
               this.children().removeIf(w -> w instanceof OptionGroupWidget || w instanceof OptionWidget);
               ((ScreenAccessor)this).getDrawables().removeIf(w -> w instanceof OptionGroupWidget || w instanceof OptionWidget);
               this.scrollAmount = 0.0F;
               this.setupModWidgets();
            } else {
               this.tabWidget.updateVisibleWidgetsForTab(selected);
               this.scrollAmount = 0.0F;
            }
            this.updateWidgetVisibilities();
         }
      }

     private void setupModWidgets() {
        this.modWidgets.clear();
        int x = 13;
        int y = 34;

        for (Mod mod : this.entryPointList.get()) {
           if (x + 140 + 13 > this.width) {
              x = 13;
              y += 47;
           }

           this.modWidgets.add(new ModWidget(mod, this, x, y));
           x += 158;
        }
     }

    public void updateWidgetVisibilities() {
        boolean isMods = false;
        if (this.showModsTab && this.tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
           isMods = categoryTab.getCategory().name().equals("Mods");
        }

        if (this.backButton != null) this.backButton.visible = this.showModsTab && isMods;
        if (this.doneButton != null) this.doneButton.visible = !this.showModsTab || !isMods;
        if (this.applyButton != null) this.applyButton.visible = !this.showModsTab || !isMods;
        if (this.undoButton != null) this.undoButton.visible = (!this.showModsTab || !isMods) && this.shouldUndoOptions();
        if (this.searchBar != null) this.searchBar.visible = !this.showModsTab || !isMods;
    }

   public void setFocusedOption(Option<?> option) {
      if (option != this.focusedOption) {
         this.focusedOption = option;
      }
   }

   public void search(String query) {
      this.allOptionWidgets.forEach(w -> w.updateSearchQuery(query));
      this.allGroupWidgets.forEach(w -> w.updateSearchQuery(query));

      for (OptionGroupWidget group : this.allGroupWidgets) {
         group.visible = group.getChildren().stream().anyMatch(w -> w.getOption().searched());
         if (group.searched(false)) {
            group.visible = true;
            group.getGroup().setExpanded(true);
            group.getChildren().forEach(w -> w.updateSearchQuery(""));
         }
      }

      String queryLower = query.toLowerCase();
      List<CategoryTab> filtered = this.allTabs.stream().filter(tab -> {
         boolean categoryMatches = tab.getCategory().name().toLowerCase().contains(queryLower);
         boolean optionMatches = tab.getCategory().optionGroups().stream().flatMap(g -> g.getOptions().stream()).anyMatch(Option::searched);
         return categoryMatches || optionMatches;
      }).toList();
      this.tabWidget.setTabs(filtered);
      if (this.tabWidget.tabSize() != this.allTabs.size()) {
         this.tabWidget.selectTab(0, true);
      }

      this.layoutGroupWidgets();
   }

   public void onChangesMade(Option<?> option) {
   }

   public void save(boolean runSave) {
      this.setOptionPrevs();
      this.configManager.get().onSave();
      if (runSave) {
         this.configManager.get().runSave();
      }

      this.defineOptions();
   }

   public void setOptionPrevs() {
      this.forEachOption(o -> o.setPrev(this.name));
   }

   public void undo() {
      this.forEachOption(Option::undo);

      for (OptionWidget widget : this.allOptionWidgets) {
         widget.onThirdPartyChange(widget.getOption().screenInstanceValue);
         if (widget instanceof PixelGridAnimationWidget w) {
            w.reset();
         }
      }

      this.setOptionPrevs();
   }

   public void resetOptions() {
      this.forEachOption(Option::reset);

      for (OptionWidget widget : this.allOptionWidgets) {
         widget.onThirdPartyChange(widget.getOption().getDefaultValue());
         switch (widget) {
            case PixelGridAnimationWidget w:
               w.reset();
               break;
             case StringListOptionWidget w:
                w.setHeight();
                break;
             default:
         }
      }

      this.setOptionPrevs();
   }

   private boolean isConfigEmpty() {
      return this.configManager.get().categories().isEmpty();
   }

   private boolean shouldResetOptions() {
      return this.anyOption(Option::hasChanged);
   }

   private boolean shouldUndoOptions() {
      return this.anyOption(o -> !o.screenInstanceCheck());
   }

   private void defineOptions() {
      this.forEachOption(Option::setScreenInstance);
   }

   private void updateScreenGlobals() {
      ScreenGlobals.OPTION_WIDTH = 320;
   }

   private void forEachOption(Consumer<Option<?>> action) {
      this.configManager.get().categories().forEach(c -> c.optionGroups().forEach(g -> g.getOptions().forEach(action)));
   }

   private boolean anyOption(Predicate<Option<?>> predicate) {
      return this.configManager.get().categories().stream().flatMap(c -> c.optionGroups().stream()).flatMap(g -> g.getOptions().stream()).anyMatch(predicate);
   }

   private boolean hasNextVisible(List<OptionWidget> children, int index) {
      for (int j = index + 1; j < children.size(); j++) {
         if (children.get(j).isVisible()) {
            return true;
         }
      }

      return false;
   }

   private int getChildHeight(OptionWidget child) {
      if (child instanceof OpenableWidget oW) {
         return (int)oW.getCurrentHeight();
      } else {
         return child instanceof StringListOptionWidget slw ? ScreenGlobals.OPTION_HEIGHT + slw.ADDITIONAL_HEIGHT : ScreenGlobals.OPTION_HEIGHT;
      }
   }

   public Graphics currentGraphicsContext() {
      return this.currentGraphicsContext;
   }
}
