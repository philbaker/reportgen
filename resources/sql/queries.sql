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
(section_1_title, section_1_text, section_2_title, section_2_text, section_3_title, section_3_text, section_4_title, section_4_text)
VALUES (:section-1-title, :section-1-text, :section-2-title, :section-2-text, :section-3-title, :section-3-text, :section-4-title, :section-4-text)

-- :name update-report! :! :n
-- :doc creates a new section
UPDATE report
SET section_1_title = :section-1-title, section_1_text = :section-1-text, section_2_title = :section-2-title, section_2_text = :section-2-text, section_3_title = :section-3-title, section_3_text = :section-3-text, section_4_title = :section-4-title, section_4_text = :section-4-text
WHERE id = :id

-- :name delete-report! :! :n
-- :doc deletes a report by id
DELETE from report WHERE id = :id

-- :name get-reports :? :*
-- :doc selects all available reports
SELECT * FROM report

-- :name get-report :? :*
-- :doc selects a single report
SELECT * FROM report WHERE id = :id

-- :name save-section! :! :n
-- :doc creates a new section
INSERT INTO section
(description, text)
VALUES (:description, :text)

-- :name update-section! :! :n
-- :doc creates a new section
UPDATE section
SET description = :description, text = :text
WHERE id = :id

-- :name delete-section! :! :n
-- :doc deletes a section by id
DELETE from section WHERE id = :id

-- :name get-sections :? :*
-- :doc selects all available sections
SELECT * FROM section

-- :name get-section :? :*
-- :doc selects a single section
SELECT * FROM section WHERE id = :id
