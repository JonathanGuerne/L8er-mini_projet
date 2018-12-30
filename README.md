# Introduction 

<table>

<tr>
<td><img src="listview.png" width="250w"></td>
<td>

La8er est un projet Android écrit avec le langage Kotlin qui a été réalisé dans le cadre du cours de système d'exploitation mobile du master MSE de la HES-SO. Il a pour but de fournir à l'utilisateur un moyen de planifier des SMS dans le temps. Cette fonctionnalité est intéressante dans beaucoup de cas différents comme : ne pas oublier d'envoyer un message important, écrire une pensée à un proche, ...
L8er est pensé pour être une application intuitive qui ne demandera à l'utilisateur que peut ou même pas d'apprentissage avant de commencer à l'utiliser. 


Dans un second temps La8er offira des fonctionalités plus avancées tel que l'envoi de SMS basé sur la géolocalisation, la création de groupe de contact ou encore la possibilité d'ajouté des variables dans le texte du message. 

</td></tr></table>

# Mise en place

Trello, un outils de gestion en ligne, à été utilisé pour simplifier la répartition des tâches sur le projet. Les tâches ont été divisées en backend et frontend regroupant respectivement les tâches liées à la logique de l'application et celle liée à l'esthétique. Il était très facile de travailler en parallèle sur le backend et le frontend car les tâches ne rentrait que rarement en conflit. 


# Technologies 

Dans un premier temps il a fallut commencer à appréhender Kotlin. Cela c'est avéré plutôt facile car il est basé sur le Java un lanage bien connu et que son intégration au projet était aussi simplifier par Android studio l'IDE utilisée pour le développement. 

## Gestion des permissions

L'application demande 3 différentes permissions : 
1. envoi de message 
2. interaction avec les contacts du téléphone
3. accès à la géolocalisation 

Les deux premières sont évidemment indispensable dans un service d'envoi de SMS planifiés et, en se qui concerne la géolocalisation, par simplification elle est également obligatoire bien qu'elle est liée à des fonctionalitées secondaire de l'application. Ceci pourrait éventuellement être changé dans les prochaines versions de l'application. 

Pour simplifier la gestion de ces exceptions un singleton ```PermissionHandler``` est utilisé. Cette classe contient les différentes fonctions permettant d'intéragir avec les permissions du téléphone nécessaire pour le bon fonctionnement de l'application. 

Dans un premier temps l'application va verifier si l'utilisateur à déjà autorisé l'accès au features clées. Si ce n'est pas le cas il faudra alors mettre en place les popup nécessaire pour inviter l'utilisateur à autoriser l'accès à ces services spécifique. Tout ce code se est écrit dans la fonction ```isPermissionGrantedAndAsk()```.

Une fois la popup de permission présentée à l'utilisateur on doit pouvoir intéragir avec ses choix. La logique appliqué pour l'instant dans cette application est que, comme expliqué plus haut, chaque permission est absolument nécessaire. Donc si l'utilisateur choisit de ne pas donner accès à un des services un message lui sera présenté pour lui signaler le problème et l'application va simplement se fermer. Ce code est écrit dans la fonction ```checkPermissionResultAndExit()```.

## Envoi de SMS
L'envoi de SMS se fait grâce au ```SmsManager``` fournit par Android. Il offre la possibilité d'envoyer un message texte à un certain numéro en passant par l'application SMS par défaut du téléphone. 

```Kotlin
SmsManager.getDefault().
  sendTextMessage(number, null, text, null, null)
```

On pourrait également spécifier des intents à lancer en cas de succès/d'échec.

## Planification d'un SMS

Le capitre précédent présentait la marche à suivre pour l'envoi d'un SMS depuis android. Il reste encore à trouver un moyen de transmettre au téléphone un ordre pour envoyer ce SMS à une date précise. 

<img src="newsms.png" width="150w" alt="Ecran de création d'un nouveau sms">

C'est le rôle du script SMSPLanner, un script mit en place pour faciliter la planification des SMS depuis n'importe quel endroit dans le projet. 

```Kotlin
fun setNewPlannedSMS(context: Context, smsModel: SMSModel, newSms: Boolean = true, update: Boolean = false)
```

la seule fonction publique du script est cette fonction```SetNewPlannedSMS```. Pernant en argument un ```SMSModel``` (voir **stockage dans la base de données**) elle va créer un pending intent content toutes les informations nécessaires à l'envoi du SMS et le transmettre à un ```AlarmManager```.
```Kotlin 
private fun getBroadcastIntent(context: Context,smsId: Int, number: String, textContent: String): Intent {
    val intent = Intent(context, SMSSenderBroadcastReceiver::class.java)
    intent.putExtra("number", number)
    intent.putExtra("textContent", textContent)
    intent.putExtra("smsId",smsId)
    return intent
}
```
Le but de l'```AlarmManager``` est de générer un intent à un moment précis. Ici, l'intent sera planifié pour la date souhaité de l'envoi du SMS. 
```Kotlin
private fun planSMS(context: Context, pIntent: PendingIntent, date: Long, showToast: Boolean) {

    val am = context.getSystemService(Context.ALARM_SERVICE)
    if (am is AlarmManager) {
        am.set(AlarmManager.RTC, date, pIntent)
        if (showToast) {
            Toast.makeText(context, "SMS Planned", Toast.LENGTH_SHORT).show()
        }
    }
}
```

Plus précisément le pending intent est attaché à un broadcast intent. Donc quand l'alarm manager va lancer l'intent c'est en fait un signal broadcast qui sera transmit. 

L'application peut facilement s'abonner à ces signaux broadcast pour pouvoir, même si elle n'est pas au premier plan, gérer la logique nécessaire. 


## Stockage dans la base de données

Le système présenté dans le chapitre précédent a un point faible : les SMS planifiés seront supprimés si le téléphone est redémarré. Pour ne pas perdre ces SMS il faut mettre en place un système de persistance. 

Chaque SMS est donc sauvegardée dans une base de données SQLite avant sont envoi. L'intérêt est finalement double : 
1. Permettre la persistance des données entre les redémarrage de l'appareil
2. Facilement récupérer les informations sur les SMS planifiés depuis l'UI. 

le modèle de SMS créé dans la base de donnée est très simple. Il comprendre un **texte** de contenu, un **numéro** de destinataire, une **date** d'envoi et le **nom** du contact. Le nom du contact est exclusivement utilisé dans l'UI. 

Cependant stocker les SMS dans une base de données n'est pas suffisant pour qu'il se "replanifient" automatiquement quand le téléphone va redémarrer. Qui plus est il peut même être dangereux d'attendre que l'utilisateur ouvre l'application avant de replanifier les SMS. Il faut un système qui puissent automatiquement replanifier les SMS au démarrage de l'appareil sans même que l'utilisateur ait besoin d'ouvir l'application. 

Cela est possible grâce au signal broadcast ```android.intent.action.BOOT_COMPLETED``` fournit par android et dont on peut s'abonner en ajoutant simplement les lignes nécessaires dans le manifest de son application. Ce signal, comme son nom l'indique, va être lancé que l'appareil aura complètement fini de démmarer. On pourra enusuit exécuter le code nécessaire à la récupération et l'envoi des SMS stockés dans la base de données. 

```Kotlin
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val smsDBHelper = SMSDBHelper(context)
            val listSMS = smsDBHelper.readAllSMS()
            listSMS.forEach {
                Log.d("RestartedSMS", it.date.toString() + " " + it.receiver + " " + it.content)
                setNewPlannedSMS(context, smsModel = it, newSms = false)
            }
        }
    }
```

## Récurrence des sms planifiés

Une fonctionalité intéressante de l'application est la possibilité de planifier des sms qui vont être répéter dans le temps sur la base d'un interval sélectionné par l'utilisateur. Lutilisateur peut choisir parmis les options suivantes :
- Tous les jours
- Toutes les semaines
- Toutes les 2 semaines 
- Tous les mois
- Tous les ans

La class ```AlarmManager``` décrite plus haut offre la posibilité de générer des signaux qui vont de répéter dans le temps. 

``` kotlin
    val am = context.getSystemService(Context.ALARM_SERVICE)
    am.setRepeating(AlarmManager.RTC, date, interval, pIntent)
```

Ici, l'intervalle est simplement le temps en milliseconde à attendre après le dernier intent envoyé. 

Pour ajouter à chaque SMS la possibilité d'avoir de la récurrence la base de donnée à dû aussi être modifiée. Un champ interval à été ajouté, par convention quand ça valeur est de -1 cela signifie que l'utilisateur n'a pas ajouté de récurrence pour son SMS.

Dans un second temps l'interface de création de SMS à dû être adaptée. Les différentes options d'intervales citées plus haut ont été stockés dans un ```Spinner```, à chaque élément de ce ```Spinner``` est assimilé une valeur en ```Long``` équivalente à la quantité de milliseconde dans le laps de temps associé. Cette information pourra ensuite facilement être transmise au ```SMSPlanner``` pour qu'il puisse donner les bons paramètres à l'```AlarmManager```. 

## Affichage des SMS à la page d'accueil 


# Conclusion 

## Avis personnels 
La création de cette application nous a permis d'apprendre beaucoup de chose. L'idée de ce projet n'était pas focrément de créer le concept le plus compliqué possible mais plutôt de faire une application "simple" mais bien faite. Nous sommes très satisfait du résultat car il répond à nos attentes du début du projet. 

L'application va pouvoir être concrètement utile et nous nous réjouissons de pouvoir continuer à l'améliorer et pourquoi pas même la partager publiquement. Il est très important pour nous que les utiliateurs aient une intuitive et bonne expérience en utilsant notre application. 

## Améliorations possibles 

Comme mentionné dans l'introduction une des améliorations possibles serait l'ajout d'un système permettant d'envoyer un sms en fonction de la géolocalisation de l'utilisateur. Egalement l'utilisateur pourrait créer des groupes de contacts qui permetteraient de facilement envoyer des messages planifiés à tout un ensemble de personnes. 
