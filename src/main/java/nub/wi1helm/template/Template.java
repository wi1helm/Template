package nub.wi1helm.template;

import nub.wi1helm.template.inventory.TemplateHandler;
import nub.wi1helm.template.npc.TemplateNPCHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Template {

    public static final Logger logger = LoggerFactory.getLogger(Template.class);

    public static void init() {
        logger.info("Template initialized");

        TemplateHandler.initialize();
        TemplateNPCHandler.initialize();
    }
}