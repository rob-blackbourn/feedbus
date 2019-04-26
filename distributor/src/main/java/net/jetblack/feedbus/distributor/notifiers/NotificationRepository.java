package net.jetblack.feedbus.distributor.notifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;

/**
 * The notification repository.
 */
public class NotificationRepository {

    private final Map<String, Set<Interactor>> _feedToNotifiables = new HashMap<String, Set<Interactor>>();

    /**
     * Remove an interactor.
     * @param interactor The interactor to remove.
     */
    public void removeInteractor(Interactor interactor) {
        // Remove the interactor where it appears in the notifiables, remembering any topics which are left without any interactors.
        Set<String> topicsWithoutInteractors = new HashSet<String>();
        for (Map.Entry<String, Set<Interactor>> topicPatternToNotifiable : _feedToNotifiables.entrySet())
        {
        	Set<Interactor> notifiables = topicPatternToNotifiable.getValue();
        	
        	if (!notifiables.contains(interactor)) {
        		continue;
        	}
        	
        	notifiables.remove(interactor);
            if (notifiables.isEmpty())
                topicsWithoutInteractors.add(topicPatternToNotifiable.getKey());
        }

        // Remove any topics left without interactors.
        for (String topic : topicsWithoutInteractors) {
            _feedToNotifiables.remove(topic);
        }
    }

    /**
     * Add a notification request.
     * @param notifiable The interactor to be notified.
     * @param feed The feed name.
     * @return true if this is new.
     */
    public boolean addRequest(Interactor notifiable, String feed)
    {
        // Find or create the set of notifiables for this feed.
        Set<Interactor> notifiables = _feedToNotifiables.get(feed);
        if (notifiables == null) {
            _feedToNotifiables.put(feed, notifiables = new HashSet<Interactor>());
        }
        else if (notifiables.contains(notifiable)) {
            return false;
        }

        // Add to the notifiables for this topic pattern and inform the subscription manager of the new notification request.
        notifiables.add(notifiable);
        return true;
    }

    /**
     * Remove a notification request.
     * @param notifiable The interactor which no longer wants to be notified.
     * @param feed The feed name.
     */
    public void removeRequest(Interactor notifiable, String feed) {
        // Does this feed have any notifiable interactors?
       Set<Interactor> notifiables = _feedToNotifiables.get(feed);
        if (notifiables == null) {
            return;
        }

        // Is this interactor in the set of notifiables for this feed?
        if (!notifiables.contains(notifiable)) {
            return;
        }

        // Remove the interactor from the set of notifiables.
        notifiables.remove(notifiable);

        // Are there any interactors left listening to this feed?
        if (!notifiables.isEmpty())
            return;

        // Remove the empty pattern from the caches.
        _feedToNotifiables.remove(feed);
    }

    /**
     * Find interactors who wish to be notified about subscriptions to a feed.
     * @param feed The feed name.
     * @return The interested interactors.
     */
    public Set<Interactor> findNotifiables(String feed) {
        Set<Interactor> interactors = _feedToNotifiables.get(feed);
        return interactors == null ? null : interactors;
    }
}
