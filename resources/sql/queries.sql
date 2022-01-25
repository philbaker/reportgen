-- :name save-message! :! :n
-- :doc creates a new message
INSERT INTO guestbook
(name, message)
VALUES (:name, :message)

-- :name get-messages :? :*
-- :doc selects all available messages
SELECT * FROM guestbook

-- :name save-report! :! :n
-- :doc creates a new report
INSERT INTO report
(section_1_text, section_2_text, section_3_text, section_4_text)
VALUES (:section-1-text, :section-2-text, :section-3-text, :section-4-text)

-- :name get-reports :? :*
-- :doc selects all available reports
SELECT * FROM report

-- :name save-section! :! :n
-- :doc creates a new message
INSERT INTO section
(description, text)
VALUES (:description, :text)

-- :name get-sections :? :*
-- :doc selects all available sections
SELECT * FROM section
