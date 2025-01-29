package nub.wi1helm.template.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemplateText {

    private final Map<Integer, Entity> text;

    private TemplateText() {
        text = new HashMap<>();
    }

    public TemplateText(Component... text) {

        this.text = IntStream.range(0, text.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i + 1,           // Key mapper: 1-based index
                        i -> createEntity(text[i])
                ));
    }



    public void setRow(Integer row, Component component) {
        if (text.containsKey(row)){
            text.get(row).editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(component));

        }
    }
    public Map<Integer, Entity> getText() {
        return text;
    }

    public Entity getRow(Integer row) {
        return text.get(row);
    }

    public Boolean isEmpty(){
        return text.isEmpty();
    }

    public static TemplateText empty(){
        return new TemplateText();
    }

    private Entity createEntity(Component component) {
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);

        entity.editEntityMeta(TextDisplayMeta.class, meta -> {
           meta.setText(component);
           meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
           meta.setHasNoGravity(true);
        });

        return entity;
    }

}
