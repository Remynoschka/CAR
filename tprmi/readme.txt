CAR TP3 : RMI

FRANCOIS Remy
CHASTE Maxime

16/04/2014



=====
Introduction
=====
Le but de ce TP est de transmettre un message sur plusieurs site
via le système RMI. Ce message doit pouvoir se transmettre sur des
architectures en arbre, ainsi que sur des architecture en graphe.

Pour lancer ce programme, il faut vous rendre dans le repertoire bin
et ensuite lancez les scripts suivants selon ce que vous voulez tester :

Arbre et parcours depuis la racine :
- creationArbre
- testRoot

Arbre est parcours depuis un noeud quelconque :
- creationArbre
- testNonRoot

Graphe :
- testGraphe

Cependant, il faudra supprimer les fichiers crees par le script creationArbre
a chaque nouveau test.


=====
Code Samples
=====
Le code suivant permet pour une architecture en graphe de diffuser 
un message vers un site

graphe.DiffuseurMessage.main()

SiteItf noeud = (SiteItf) Naming.lookup(argv[0]);
Date d = new Date();
noeud.propager(argv[1].getBytes(), Integer.parseInt(argv[0]),
new Timestamp(d.getTime()));

----------

Le code suivant permet pour une architecture en graphe de propager
un message vers les noeuds voisins

graphe.SiteImpl.propager

for (SiteItf voisin : this.voisins) {
	if (voisin.getId() != idEnvoyeur) {
		mess += voisin.getId() + ", ";
		voisin.propager(message, this.id, ts);
	}
}

----------

Le code suivant permet pour une architecture en arbre de propager
un message vers les noeuds fils

arbre.SiteImpl.propager

if (!tsTab.contains(ts)) {
	tsTab.add(ts);
	String t = new String(message);
	String mess = "";
	String enfants = "";
	String envoye = "\n" + "J'envoie le message a mes fils n°";e = "\n" + "J'envoie le message a mes fils n°";

	...

	if (pere != null && idEnvoyeur != pere.getId()) {
		mess += ", Mon pere est le noeud nÂ°" + pere.getId()
			+ " et je lui envoie le message ";
		if (this.fils.isEmpty()) {
			mess += "Mes enfants sont : \n";
			for (SiteItf enfant : this.fils) {
				enfants += enfant.getId() + ", ";
			}
		} else
			mess += "je n'ai pas d'enfant";
		pere.propager(message, this.id, ts);
	}

	...
	for (SiteItf enfant : this.fils) {
		enfants += enfant.getId() + ", ";
		if (enfant.getId() != idEnvoyeur) {
			envoye += enfant.getId() + ", ";
			enfant.propager(message, this.id, ts);
		}
	}
}