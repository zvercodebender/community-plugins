import os
from com.xebialabs.deployit.plugin.api.reflect import DescriptorRegistry

links = {}
types = set()
prefixes = set()
generate_subgraphs = 0

fontname = "Bitstream Vera Sans"
fontsize = 8
colorscheme = "paired12"
#colorize only core types and bundled plugins, the other will remain black.
colormap = {'udm': 4, 'cloud': 1, 'mail': 2, 'core': 3, 'file': '8', 'sql': 9, 'www': 10, 'jee': 12, 'overthere': 5, 'generic': 6, 'internal': 4}


def get_color(t):
    prefix = t.getPrefix()
    if prefix in colormap:
        return colormap[prefix]
    else:
        return 0


for d in DescriptorRegistry.getDescriptors():
    hlist = [d.getType()]
    hlist.extend(d.getSuperClasses())
    ctype = None
    for h in hlist:
        if ctype is not None:
            links[ctype] = h
        ctype = h
        types.add(ctype)
        prefixes.add(str(ctype.getPrefix()))

gvscript = ["digraph udm {", 'fontname = "%s" ' % fontname, "fontsize = %s " % fontsize]
gvscript.append('node [ fontname = "%s" fontsize = %s shape = "record" ]' % (fontname, fontsize))
gvscript.append('edge [ fontname = "%s" fontsize = %s  ]' % (fontname, fontsize))
for k, v in links.items():
    gvscript.append('"%s" -> "%s";' % (k, v))
    gvscript.append('"%s" [shape=box colorscheme="%s" color=%s]; ' % (k, colorscheme, get_color(k)))
    gvscript.append('"%s" [shape=box colorscheme="%s" color=%s]; ' % (v, colorscheme, get_color(v)))

if generate_subgraphs:
    for p in prefixes:
        gvscript.append('subgraph cluster_%s {' % p)
        for t in types:
            if str(t.getPrefix()) == p:
                gvscript.append('"%s" [shape=box]; ' % t)
        gvscript.append('}')

gvscript.append("}")

#print '\n'.join(gvscript)

text_file = open("deployit-type-system.gv", "w")
text_file.write('\n'.join(gvscript))
text_file.close()

cmd="dot -T png -o deployit-type-system.png deployit-type-system.gv"
print "Execute the command %s" %  cmd
os.system(cmd)

