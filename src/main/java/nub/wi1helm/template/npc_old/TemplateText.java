package nub.wi1helm.template.npc_old;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import nub.wi1helm.template.npc_old.entities.TemplateTextEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemplateText {

    private final Map<Integer, TemplateTextEntity> text;

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
    public Map<Integer, TemplateTextEntity> getText() {
        return text;
    }

    public TemplateTextEntity getRow(Integer row) {
        return text.get(row);
    }

    public Boolean isEmpty(){
        return text.isEmpty();
    }

    public static TemplateText empty(){
        return new TemplateText();
    }

    private TemplateTextEntity createEntity(Component component) {
        return new TemplateTextEntity(component);
    }

}
