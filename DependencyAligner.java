///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.apache.maven:maven-core:3.9.0
//DEPS org.apache.maven:maven-model:3.9.0
//DEPS org.apache.maven:maven-settings:3.9.0

package springboot.camel;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remove dependency from an help:effective-pom
 *
 * args[0] pom.xml path
 * args[1] comma separated list of groupIds to remove (contains is used)
 */
public class DependencyAligner {

    public static void main(String[] args) throws Exception {
        Model model = null;
        String pom = args[0];
        String[] groupIds = args[1].split(",");
        try (InputStream is = new FileInputStream(pom)) {
            model = new MavenXpp3Reader().read(is);

            Set<Dependency> dependenciesToRemove = model.getDependencies().stream()
                    .filter(dependency -> {
                for (String groupId : groupIds) {
                    if (dependency.getGroupId().contains(groupId)) {
                        return true;
                    }
                }

                return false;
            }).collect(Collectors.toSet());

            for (Dependency dependency : dependenciesToRemove) {
                model.removeDependency(dependency);
            }
        }

        try (OutputStream os = new FileOutputStream(pom)) {
            new MavenXpp3Writer().write(os, model);
        }
    }
}
