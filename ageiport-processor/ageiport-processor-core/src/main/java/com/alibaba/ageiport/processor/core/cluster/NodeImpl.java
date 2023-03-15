package com.alibaba.ageiport.processor.core.cluster;

import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class NodeImpl implements Node {

    private String app;
    private String id;

    private String host;

    private String group;

    private Map<String, String> labels;

    private Map<String, String> attributes;


    public NodeImpl() {

    }

    public NodeImpl(String id, String host, String group, Map<String, String> labels, Map<String, String> attributes) {
        this.id = id;
        this.host = host;
        this.group = group;
        this.labels = labels;
        this.attributes = attributes;
    }
}
